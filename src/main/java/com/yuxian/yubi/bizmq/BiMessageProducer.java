package com.yuxian.yubi.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yuxian&羽弦
 * date 2023/06/26 12:17
 * description:
 * @version 1.0
 **/
@Component
public class BiMessageProducer {

	@Resource
	private RabbitTemplate rabbitTemplate;

	/**
	 * 发送消息
	 *
	 * @param message
	 */
	public void sendMessage(String message) {
		rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, message);
	}
}
