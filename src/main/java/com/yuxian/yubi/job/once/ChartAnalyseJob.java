package com.yuxian.yubi.job.once;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yuxian.yubi.api.OpenAiApi;
import com.yuxian.yubi.enums.AIModelEnum;
import com.yuxian.yubi.enums.ChartStatusEnum;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.BusinessException;
import com.yuxian.yubi.exception.ThrowUtils;
import com.yuxian.yubi.model.entity.Chart;
import com.yuxian.yubi.service.ChartAnalyseOverflowService;
import com.yuxian.yubi.service.ChartService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Callable;

/**
 * @author yuxian&羽弦
 * date 2023/06/14 20:52
 * description:
 * @version 1.0
 **/
@Data
public class ChartAnalyseJob implements Callable<Boolean> {

	/**
	 * 图表Id
	 */
	private Long chartId;

	private OpenAiApi openAiApi;

	private String question;

	private ChartService chartService;

	private ChartAnalyseOverflowService chartAnalyseOverflowService;

	public ChartAnalyseJob(Long chartId, OpenAiApi openAiApi, String question, ChartService chartService, ChartAnalyseOverflowService chartAnalyseOverflowService) {
		this.chartId = chartId;
		this.openAiApi = openAiApi;
		this.question = question;
		this.chartService = chartService;
		this.chartAnalyseOverflowService = chartAnalyseOverflowService;
	}

	@Override
	public Boolean call() {
		String[] results = openAiApi.genChartAnalyse(AIModelEnum.CHART_MODEL.getId(), question);

		// 生成图表类
		LambdaUpdateWrapper<Chart> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Chart::getId, chartId).set(Chart::getGenChart, results[1]).set(Chart::getGenResult, results[2]).set(Chart::getStatus, ChartStatusEnum.SUCCEED.getCode());
		boolean updateResult = chartService.update(updateWrapper);
		ThrowUtils.throwIf(!updateResult, ErrorCode.SYSTEM_ERROR, "分析结果更新数据库失败");
		return true;
	}

}
