package cn.t.mq.consumer.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cn.t.mq.properties.MqProperties;
import cn.t.redis.util.RedisUtil;

@Component
public class TestReceiver {

	@Autowired
	private RedisUtil util;

	/**
	 * 监听死信队列,处理
	 * 
	 * @param testMessage
	 * @throws IOException
	 */
	@RabbitListener(queues = "dead.queue")
	public void deadQueueMonitorProcess(Message message, Channel channel) throws IOException {
		System.out.println("消费者收到消息  : " + message);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

	}

	// @RabbitListener(queues = "topic.queue.t1")
	public void process(Map<String, Object> testMessage) {
		System.out.println("消费者收到消息  : " + testMessage.get("test"));
	}

	/**
	 * 注入Channel后可以进行手动确认,这个方法能够正常运行的前提是yml文件配置了手动确认
	 * 
	 * @param message
	 * @param channel
	 * @throws IOException
	 */
	@RabbitListener(queues = "topic.queue.t1")
	public void process(Message message, Channel channel) throws IOException {
		try {
			// 这里可以根据不同的队列名进行分支处理不同的业务逻辑,前提是Config中设置了多队列
			System.out.println("接收到消息的消息来自:" + message.getMessageProperties().getConsumerQueue());
			System.out.println("消费者收到消息  : " + message);
			// Jackson2JsonMessageConverter这个转换器是SpringBoot-amqp提供的消息转换类
			System.out.println("转换回原来的对象:" + new Jackson2JsonMessageConverter().fromMessage(message));
			System.out.println(
					"转换回原来的对象并取出属性:" + ((User) new Jackson2JsonMessageConverter().fromMessage(message)).getName());

			System.out.println("messageId:" + message.getMessageProperties().getMessageId());
			if (message.getMessageProperties().getMessageId() == null) {
				// 头部没有messageId则转入死信队列
				channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
				return;
			}
			// 手动确认，第二个参数指代本次确认只确认当前信息,避免线程问题
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			e.printStackTrace();
			if (message.getMessageProperties().isRedelivered()) {
				System.out.println("消息被重新投递");
			}
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
			// 第三个参数为false则代表不入当前队列,丢弃入死信队列
			// channel.basicNack(deliveryTag, false, true);
		}
	}

	// @RabbitListener(queues = "topic.queue.t1")
	public void process(User user) {
		// 这里使用默认的消息转换器可以做到自动转换
		System.out.println("消费者收到消息  : " + user.getName());
	}

	// @RabbitListener(queues = "topic.queue.t1")
	public void process(List<String> list) {
		this.util.setListR("cacheLists", list, 300);
		System.out.println("消费者收到消息  : " + list);
	}

}
