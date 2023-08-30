package com.yuxian.yubi.aop;

import com.yuxian.yubi.annotation.RequestLimit;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.ThrowUtils;
import com.yuxian.yubi.manager.RedissonLimitManager;
import com.yuxian.yubi.model.entity.User;
import com.yuxian.yubi.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author yuxian&羽弦
 * date 2023/06/06 11:22
 * description: 限制单个方法单个用户的请求次数
 * @version 1.0
 **/
@Aspect
@Component
public class RequestLimitInterceptor {

	@Resource
	private RedissonLimitManager redissonLimitManager;
	@Resource
	private UserService userService;

	@Before("@annotation(requestLimit)")
	public void requestBefore(JoinPoint joinPoint, RequestLimit requestLimit) {
		HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[2];
		User loginUser = userService.getLoginUser(request);
		ThrowUtils.throwIf(Objects.isNull(loginUser), ErrorCode.NO_AUTH_ERROR);

		String methodName = joinPoint.getSignature().getName();
		redissonLimitManager.doRateLimit(methodName + "_" + loginUser.getId());
	}

}
