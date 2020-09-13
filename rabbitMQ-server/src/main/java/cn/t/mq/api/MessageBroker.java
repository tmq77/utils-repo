package cn.t.mq.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.t.mq.consumer.test.User;
import cn.t.mq.properties.MqProperties;

@RestController
public class MessageBroker {

	/**
	 * 队列以及交换机名的配置
	 */
	@Autowired
	private MqProperties mqProperties;

	/**
	 * 使用RabbitTemplate,这提供了接收/发送等等方法
	 */
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@PostMapping("/sendMessageByTopicWithString")
	public Map<String, Object> sendMsgByTopicWithString() {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			this.rabbitTemplate.convertAndSend(this.mqProperties.getTopicExchange(), this.mqProperties.getTopicQueue(),
					"hello world!");
			resultMap.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", "failure");
		}
		return resultMap;
	}

	@PostMapping("/sendMessageByTopicWithMap")
	public Map<String, Object> sendMsgByTopicWithMap() {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			Map<String, Object> msgMap = new HashMap<>();
			msgMap.put("test", "hello world");
			this.rabbitTemplate.convertAndSend(this.mqProperties.getTopicExchange(), this.mqProperties.getTopicQueue(),
					msgMap);
			resultMap.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", "failure");
		}
		return resultMap;
	}

	/**
	 * 发送序列化后的pojo类对象
	 * 
	 * @return
	 */
	@PostMapping("/sendMessageByTopicWithObject")
	public Map<String, Object> sendMsgByTopicWithObject() {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 因为使用的是默认的转换器
			// 这里传送的Object对象必须实现Serializable接口，否则会抛出异常
			// SimpleMessageConverter only supports String, byte[] and Serializable payloads
			// 或者自定义消息转换器来处理
			User user = new User();
			user.setId(1);
			user.setName("tmq");
			this.rabbitTemplate.convertAndSend(this.mqProperties.getTopicExchange(), this.mqProperties.getTopicQueue(),
					user, message -> {
						// 设置messageId
						message.getMessageProperties().setMessageId("messageId");
						// 设置过期时间
						message.getMessageProperties().setExpiration("5000");
						return message;
					});

			resultMap.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", "failure");
		}
		return resultMap;
	}

	@PostMapping("/sendMessageByTopicWithList")
	public Map<String, Object> sendMsgByTopicWithList(@RequestBody List<String> list) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 这里传送的Object对象必须实现Serializable接口，否则会抛出异常
			// SimpleMessageConverter only supports String, byte[] and Serializable payloads
			// 或者自定义消息转换器来处理
			this.rabbitTemplate.convertAndSend(this.mqProperties.getTopicExchange(), this.mqProperties.getTopicQueue(),
					list, new CorrelationData());
			/*
			 * this.rabbitTemplate.convertAndSend(this.mqProperties.getTopicExchange(),
			 * this.mqProperties.getTopicQueue(), list);
			 */
			resultMap.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", e.getMessage());
		}
		return resultMap;
	}

	/**
	 * 消费者手动确认测试
	 * 
	 * @return
	 */
	@PostMapping("/manual")
	public Map<String, Object> manual() {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 这里传送的Object对象必须实现Serializable接口，否则会抛出异常
			// SimpleMessageConverter only supports String, byte[] and Serializable payloads
			// 或者自定义消息转换器来处理
			User user = new User();
			user.setId(1);
			user.setName("tmq");

			CorrelationData data = new CorrelationData("111112222");

			this.rabbitTemplate.convertAndSend(this.mqProperties.getTopicExchange(), this.mqProperties.getQueueManual(),
					user, data);
			resultMap.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", "failure");
		}
		return resultMap;
	}

	/**
	 * 调用下面的接口会触发ReturnCallBack,因为消息没有送达到队列
	 * 
	 * @return
	 */
	@PostMapping("/testReturnCallBack")
	public Map<String, Object> testReturnCallBack() {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 这里传送的Object对象必须实现Serializable接口，否则会抛出异常
			// SimpleMessageConverter only supports String, byte[] and Serializable payloads
			// 或者自定义消息转换器来处理
			User user = new User();
			user.setId(1);
			user.setName("tmq");
			this.rabbitTemplate.convertAndSend("amq.topic", this.mqProperties.getTopicQueue(), user);
			resultMap.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", "failure");
		}
		return resultMap;
	}
}
