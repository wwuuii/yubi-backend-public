package com.yuxian.yubi.job.once;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yuxian.yubi.bizmq.BiMqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuxian&羽弦
 * date 2023/07/25 15:37
 * description:
 * @version 1.0
 **/
@Component
@Slf4j
public class MQInitJob {

	@PostConstruct
	public void init() {
		try {
			log.info("start init mq");
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			log.info("channel{}", channel.toString());
			String EXCHANGE_NAME = BiMqConstant.BI_EXCHANGE_NAME;
			//创建图表分析任务交换机
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
			// 创建死信交换机
			channel.exchangeDeclare(BiMqConstant.BI_DEAD_EXCHANGE_NAME, "direct");
			//创建死信队列
			channel.queueDeclare(BiMqConstant.BI_DEAD_QUEUE_NAME, true, false, false, null);
			channel.queueBind(BiMqConstant.BI_DEAD_QUEUE_NAME, BiMqConstant.BI_DEAD_EXCHANGE_NAME, BiMqConstant.BI_DEAD_ROUTING_KEY);
			// 创建图表分析任务队列
			String queueName = BiMqConstant.BI_QUEUE_NAME;
			Map<String, Object> arguments = new HashMap<>();
			arguments.put("x-dead-letter-exchange", BiMqConstant.BI_DEAD_EXCHANGE_NAME);
			arguments.put("x-dead-letter-routing-key", BiMqConstant.BI_DEAD_ROUTING_KEY);
			channel.queueDeclare(queueName, true, false, false, arguments);
			channel.queueBind(queueName, EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY);
			channel.close();
			log.info("init success");
		} catch (Exception e) {
			log.error("init mq error,{}", e.getMessage());
		}
	}
}
