package cn.t.mq.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:mq.properties")
public class MqProperties {
	
	/**
	 * routing_key 队列绑定的监听键,路由键 队列名
	 */
	@Value("${topic.queue}")
	private String topicQueue;
	
	/**
	 * 手动确认的消息队列
	 */
	@Value("${topic.queue.manual}")
	private String queueManual;
	
	@Value("${dead.queue}")
	private String deadQueue;
	
	public String getDeadQueue() {
		return deadQueue;
	}

	public void setDeadQueue(String deadQueue) {
		this.deadQueue = deadQueue;
	}

	public String getDeadExchange() {
		return deadExchange;
	}

	public void setDeadExchange(String deadExchange) {
		this.deadExchange = deadExchange;
	}

	@Value("${dead.exchange}")
	private String deadExchange;

	public String getQueueManual() {
		return queueManual;
	}

	public void setQueueManual(String queueManual) {
		this.queueManual = queueManual;
	}

	/**
	 * 交换机名
	 */
	@Value("${topic.exchange}")
	private String topicExchange;

	public String getTopicQueue() {
		return topicQueue;
	}

	public void setTopicQueue(String topicQueue) {
		this.topicQueue = topicQueue;
	}

	public String getTopicExchange() {
		return topicExchange;
	}

	public void setTopicExchange(String topicExchange) {
		this.topicExchange = topicExchange;
	}

}
