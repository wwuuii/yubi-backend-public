package com.yuxian.yubi.job.cycle;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yuxian&羽弦
 * date 2023/06/18 09:40
 * description: 监听任务队列的长度，动态调整核心线程数
 * @version 1.0
 **/
@Configuration
@ConfigurationProperties("monitor.queue")
@Data
public class MonitorTaskQueueJob {

	@Resource
	private ThreadPoolExecutor threadPoolExecutor;
	// 当任务队列数超过queueSizeThreshold时，增加核心线程数
	private Integer queueSizeThreshold = 6;
	// 核心线程数不能超过该值
	private Integer maxThreadNum = 10;
	// 核心线程数不能低于该值
	private Integer minThreadNum = 4;

	@Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
	public void adjustThreadPool() {
		int queueSize = threadPoolExecutor.getQueue().size();
		int corePoolSize = threadPoolExecutor.getCorePoolSize();
		if (queueSize > queueSizeThreshold) {
			if (corePoolSize < maxThreadNum) {
				threadPoolExecutor.setCorePoolSize(corePoolSize + 1);
			}
		} else if (queueSize == 0) {
			if (corePoolSize > minThreadNum) {
				threadPoolExecutor.setCorePoolSize(corePoolSize - 1);
			}
		}
	}

}
