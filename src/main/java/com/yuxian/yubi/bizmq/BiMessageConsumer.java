package com.yuxian.yubi.bizmq;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.rholder.retry.Retryer;
import com.rabbitmq.client.Channel;
import com.yuxian.yubi.api.OpenAiApi;
import com.yuxian.yubi.enums.AIModelEnum;
import com.yuxian.yubi.enums.ChartStatusEnum;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.BusinessException;
import com.yuxian.yubi.exception.ThrowUtils;
import com.yuxian.yubi.mapper.ChartMapper;
import com.yuxian.yubi.model.entity.Chart;
import com.yuxian.yubi.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author yuxian&羽弦
 * date 2023/06/26 12:21
 * description:
 * @version 1.0
 **/
@Component
@Slf4j
public class BiMessageConsumer {

	@Resource
	private OpenAiApi openAiApi;
	@Resource
	private ChartMapper chartMapper;
	@Resource
	private ChartService chartService;
	@Resource
	private Retryer<Boolean> retryer;
	@Resource
	private ThreadPoolExecutor threadPoolExecutor;

	@RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
	@SneakyThrows
	public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
		if (StringUtils.isBlank(message)) {
			channel.basicNack(deliveryTag, false, false);
		}
		CompletableFuture.runAsync(() -> {
			try {
				retryer.call(() -> {
					handleMessage(message);
					channel.basicAck(deliveryTag, false);
					return true;
				});
			} catch (Exception e) {
				try {
					channel.basicNack(deliveryTag,  false, false);
				} catch (Exception ackException) {
				}
				log.error("BiMessageConsumer.class  " + e.getMessage());
			}
		}, threadPoolExecutor);
	}

	private void handleMessage(String message) {
		log.info("开始执行");
		Chart chart = getChartByBiMessage(message);
		ThrowUtils.throwIf(chart == null, ErrorCode.PARAMS_ERROR, String.format("消息处理失败，消息ID为:%s的数据不存在", message));
		//生成用户需求
		String question = genUserDemand(chart.getChartData(), chart.getGoal(), chart.getChartType());
		String[] results = openAiApi.genChartAnalyse(AIModelEnum.CHART_MODEL.getId(), question);
		log.info("结果");
		// 生成图表类
		LambdaUpdateWrapper<Chart> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Chart::getId, chart.getId()).set(Chart::getGenChart, results[1]).set(Chart::getGenResult, results[2]).set(Chart::getStatus, ChartStatusEnum.SUCCEED.getCode());
		boolean updateResult = chartService.update(updateWrapper);
		ThrowUtils.throwIf(!updateResult, ErrorCode.SYSTEM_ERROR, "分析结果更新数据库失败");
	}

	public Chart getChartByBiMessage(String message) {
		long id;
		try {
			id = Long.parseLong(message);
		} catch (NumberFormatException e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息处理失败，消息ID为:" + message + "的数据不存在");
		}
		return chartMapper.selectById(id);
	}

	private String genUserDemand(String csvData, String goal, String chartType) {
		StringBuilder question = new StringBuilder();
		question.append("分析需求：\n").append("{").append(goal)
				.append(",生成").append(chartType).append("}\n")
				.append("原始数据:\n").append("{").append(csvData).append("}");
		return question.toString();
	}
}
