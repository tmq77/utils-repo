package cn.t.mq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.t.mq.consumer.test.TestAckReceiver;
import cn.t.mq.properties.MqProperties;

/**
 * 消费者手动确认消息的配置类，用了这个类后,消费者监听消息就需要自己实现ChannelAwareMessageListener接口
 * 也可以直接在消费者处注入channel进行手动确认,具体参照TestReceiver.java
 * @author tmq
 *
 */
//@Configuration
public class MsgListenerConfig {
	
	@Autowired
	private MqProperties mqProperties;
	
	/**
	 * 因为这个配置类和消息生产者在一起,所以这里注入的是RabbitMQConfig内配置的ConnectionFactory
	 */
	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer () {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		// RabbitMQ默认是自动确认，这里改为手动确认消息
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); 
		//设置一个队列
        container.setQueueNames(this.mqProperties.getQueueManual());
        //如果同时设置多个如下： 前提是队列都是必须已经创建存在的
        //  container.setQueueNames("TestQueue1","TestQueue2","TestQueue3");
        //另一种设置队列的方法,如果使用这种情况,那么要设置多个,就使用addQueues
        //container.setQueues(new Queue("TestQueue1",true));
        //container.addQueues(new Queue("TestQueue2",true));
        
        container.setMessageListener(new TestAckReceiver());
        
        return container;
	}

}
