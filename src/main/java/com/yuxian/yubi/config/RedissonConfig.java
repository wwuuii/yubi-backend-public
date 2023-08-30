package com.yuxian.yubi.config;

import io.swagger.models.auth.In;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuxian&羽弦
 * date 2023/06/06 10:56
 * description:
 * @version 1.0
 **/
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

	private Integer database;
	private String host;
	private Integer port;
	private String password;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + host + ":" + port);
		return Redisson.create(config);
	}
}
