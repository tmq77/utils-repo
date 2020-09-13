package cn.t.mq.consumer.test;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.rabbitmq.client.Channel;

public class TestAckReceiver implements ChannelAwareMessageListener {

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		// TODO Auto-generated method stub
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		try {
			System.out.println("接收到的消息唯一ID:" + deliveryTag);
			System.out.println("接收到的消息ID:" + message.getMessageProperties().getMessageId());
			System.out.println("消息的用户ID:" + message.getMessageProperties().getUserId());
			
			// 这里可以根据不同的队列名进行分支处理不同的业务逻辑,前提是Config中设置了多队列
			System.out.println("接收到消息的消息来自:" + message.getMessageProperties().getConsumerQueue());
			
			System.out.println(new Jackson2JsonMessageConverter().fromMessage(message));
			// 第二个参数设置false,尽量一次处理一条消息,否则可能导致多线程问题
			channel.basicAck(deliveryTag, false);
			
			//为true会重新放回队列，false则丢弃该消息
			// channel.basicReject(deliveryTag, true);
			// 第一个参数是当前消息到的数据的唯一id;
			// 第二个参数是指是否针对多条消息；如果是true，也就是说一次性针对当前通道的消息的tagID小于当前这条消息的，都拒绝确认。
			// 第三个参数是指是否重新入列，也就是指不确认的消息是否重新丢回到队列里面去
			// channel.basicNack(deliveryTag, false, true);
		} catch (Exception e) {
			// 出现异常则拒绝此条消息,并且丢弃
			channel.basicReject(deliveryTag, false);
			e.printStackTrace();
		}
		

	}


}
