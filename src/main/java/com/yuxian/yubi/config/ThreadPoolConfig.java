package com.yuxian.yubi.config;

import com.yuxian.yubi.enums.ChartStatusEnum;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.ThrowUtils;
import com.yuxian.yubi.job.once.ChartAnalyseJob;
import com.yuxian.yubi.model.entity.ChartAnalyseOverflow;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author yuxian&羽弦
 * date 2023/06/13 21:55
 * description:
 * @version 1.0
 **/
@Configuration
@ConfigurationProperties("thread.pool")
@Data
public class ThreadPoolConfig {
	private String threadNamePrefix = "AI-Analyse-";
	private int corePoolSize = 4;
	private int maximumPoolSize = 6;
	private long keepAliveTime = 10L;
	private Integer capacity = 10;


	@Bean
	public ThreadPoolExecutor getThreadPoolExecutor() {
		ThreadFactory threadFactory = r -> new Thread(r, threadNamePrefix + r.hashCode());
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(capacity), threadFactory,
				(r, executor) -> {
					ThrowUtils.throwIf(!(r instanceof ChartAnalyseJob), ErrorCode.SYSTEM_ERROR, "任务不是图表分析任务");
					ChartAnalyseJob chartAnalyseJob = (ChartAnalyseJob) r;
					// 记录任务溢出
					ChartAnalyseOverflow chartAnalyseOverflow = new ChartAnalyseOverflow();
					chartAnalyseOverflow.setChartId(chartAnalyseJob.getChartId());
					chartAnalyseOverflow.setQuestion(chartAnalyseJob.getQuestion());
					chartAnalyseJob.getChartAnalyseOverflowService().save(chartAnalyseOverflow);
					//修改图表状态
					chartAnalyseJob.getChartService().updateChartStatus(chartAnalyseOverflow.getId(), ChartStatusEnum.FAILED.getCode());
				});
	}
}
