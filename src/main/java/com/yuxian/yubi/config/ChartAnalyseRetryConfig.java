package com.yuxian.yubi.config;

import com.github.rholder.retry.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author yuxian&羽弦
 * date 2023/06/15 17:09
 * description:
 * @version 1.0
 **/
@Configuration
@ConfigurationProperties("chart.retry")
@Data
public class ChartAnalyseRetryConfig {

	private Integer fixedWait = 1;
	private Integer stopAfterAttempt = 3;
	@Bean
	public Retryer<Boolean> retryer() {
		return RetryerBuilder.<Boolean>newBuilder()
				.retryIfException()
				.retryIfResult(r -> !r)
				//等待策略：每次请求间隔1s
				.withWaitStrategy(WaitStrategies.fixedWait(fixedWait, TimeUnit.SECONDS))
				//停止策略 : 尝试请求3次
				.withStopStrategy(StopStrategies.stopAfterAttempt(stopAfterAttempt))
				.build();
	}


}
