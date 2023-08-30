package com.yuxian.yubi.manager;

import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.BusinessException;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yuxian&羽弦
 * date 2023/06/06 10:53
 * description:
 * @version 1.0
 **/
@Service
public class RedissonLimitManager {

	@Resource
	private RedissonClient redissonClient;


	/**
	 * 限流操作
	 *
	 * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
	 */
	public void doRateLimit(String key) {
		RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
		rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
		boolean canOp = rateLimiter.tryAcquire();
		if (!canOp) {
			throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
		}
	}
}
