package com.yuxian.yubi.bizmq;

import com.rabbitmq.client.Channel;
import com.yuxian.yubi.enums.ChartStatusEnum;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.BusinessException;
import com.yuxian.yubi.mapper.ChartMapper;
import com.yuxian.yubi.model.entity.Chart;
import com.yuxian.yubi.service.ChartService;
import com.yuxian.yubi.service.impl.ChartServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yuxian&羽弦
 * date 2023/07/03 13:41
 * description:
 * @version 1.0
 **/
@Component
public class BiDeadMessageConsumer {

	@Resource
	private ChartService chartService;
	@Resource
	private ChartMapper chartMapper;

	@RabbitListener(queues = {BiMqConstant.BI_DEAD_QUEUE_NAME}, ackMode = "MANUAL")
	@SneakyThrows
	public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
		if (StringUtils.isBlank(message)) {
			channel.basicNack(deliveryTag, false, false);
		}
		try {
			Chart chart = getChartByBiMessage(message);
			//更新图表分析状态
			chartService.updateChartStatus(chart.getId(), ChartStatusEnum.FAILED.getCode());
			channel.basicAck(deliveryTag, false);
		} catch (Exception e) {
			channel.basicNack(deliveryTag, false, false);
		}

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
}
