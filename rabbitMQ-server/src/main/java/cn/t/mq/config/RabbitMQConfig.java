package cn.t.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.Exchange;

import cn.t.mq.properties.MqProperties;
import cn.t.mq.properties.RabbitConfigProperties;

/**
 * Topic Exchange Configuration 通配符模式(Topic)
 * ​类似路由模式，但是routing_key支持模糊匹配，按规则转发消息（最灵活）。符号“#”匹配一个或多个词，符号“*”匹配一个词
 * 例如routing_key为topic.# 样只要是消息携带的路由键是以topic.开头,都会分发到该队列
 * https://blog.csdn.net/u010365540/article/details/80438284
 * https://docs.spring.io/spring-amqp/docs/2.2.10.RELEASE/reference/html/#_with_java_configuration
 * https://www.freesion.com/article/33191068587/
 * 
 * @author tmq
 *
 */
@Configuration
public class RabbitMQConfig {

	/**
	 * 队列以及交换机名的配置
	 */
	@Autowired
	private MqProperties mqProperties;

	@Autowired
	private RabbitConfigProperties rabbitConfigProperties;

	/**
	 * 创建队列,如果RabbitMQ服务器上没有这个名字的队列,那么就会创建
	 * 
	 * @return
	 */
//	@Bean
//	public Queue topicQueue() {
//		return new Queue(this.mqProperties.getTopicQueue());
//	}

	/**
	 * 创建队列,如果RabbitMQ服务器上没有这个名字的队列,那么就会创建
	 * 
	 * @return
	 */
	@Bean
	public Queue topicManualQueue() {
		return new Queue(this.mqProperties.getQueueManual());
	}

	/**
	 * 创建交换机,如果RabbitMQ服务器上没有这个名字的交换机,那么就会创建 正常业务交换机
	 * 
	 * @return
	 */
	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(this.mqProperties.getTopicExchange());
	}

	/**
	 * 创建绑定 将队列绑定至交换机并绑定路由键
	 * 
	 * 将配置了死信的业务队列绑定到正常业务交换机
	 * 
	 * 正常的业务交换机会根据正常的路由Key发送消息到正常的业务队列
	 * 
	 * @return
	 */
	@Bean
	public Binding bindingExchange() {
		return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(this.mqProperties.getTopicQueue());
	}

	@Bean
	public Binding bindingExchange2() {
		return BindingBuilder.bind(topicManualQueue()).to(topicExchange()).with(this.mqProperties.getQueueManual());
	}

	/**
	 * 创建正常的业务队列以及绑定死信交换机信息以及死信路由键
	 */
	@Bean
	public Queue topicQueue() {
		// 配置死信队列需要先将正常队列与死信交换机建立关系
		return QueueBuilder.durable(this.mqProperties.getTopicQueue()) // 配置队列(持久化)
				// 配置死信交换机名
				.withArgument("x-dead-letter-exchange", this.mqProperties.getDeadExchange())
				.withArgument("x-dead-letter-routing-key", this.mqProperties.getDeadQueue())
				.withArgument("x-message-ttl", 20000) // 消息过期时间20S,超时消息进入死信(实际测试并没有,channel不关闭就一直在业务队列)
				.build();
	}
	
	/**
	 * 死信队列，正常业务队列中的异常消息会根据正常队列的死信绑定信息转到这里
	 * 出现死信时,正常业务队列会把死信给到死信交换机，再由死信交换机投递到这里
	 * @return
	 */
	@Bean
	public Queue deadQueue() {
		return new Queue(this.mqProperties.getDeadQueue());
	}

	/**
	 * 死信交换机 死信队列跟交换机类型没有关系 不一定为DirectExchange 不影响该类型交换机的特性
	 * 
	 * @return
	 */
	@Bean
	public DirectExchange deadExchange() {
		// 死信交换机也是一个普通的交换机
		return new DirectExchange(this.mqProperties.getDeadExchange());
	}

	/**
	 * 创建绑定,将死信接收队列绑定到死信交换机上
	 * 
	 * @return
	 */
	@Bean
	public Binding deadQueueBinding() {
		return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(this.mqProperties.getDeadQueue());
	}

	/**
	 * 自定义连接工厂类配置,当需要处理多种不同的回调时使用此配置,
	 * 并将RabbitTemplate的模式改为prototype,用于让RabbitTemplate可以执行不同的回调
	 */

	/**
	 * 自定义配置连接
	 * 
	 * @return
	 */
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(this.rabbitConfigProperties.getHost());
		connectionFactory.setPort(this.rabbitConfigProperties.getPort());
		connectionFactory.setUsername(this.rabbitConfigProperties.getUsername());
		connectionFactory.setPassword(this.rabbitConfigProperties.getPassword());
		connectionFactory.setVirtualHost(this.rabbitConfigProperties.getVirtualHost());
		connectionFactory.setPublisherConfirmType(this.rabbitConfigProperties.getPublisherConfirmType());
		// 设置ReturnCallback函数是否触发,设置true时,在消息未送达队列的情况下会触发
		connectionFactory.setPublisherReturns(this.rabbitConfigProperties.isPublisherReturns());
		return connectionFactory;
	}

	/**
	 * 使用自定义连接工厂配置RabbitTemplate 开启prototype作用域,每个RabbitTemplate都可以执行自己的回调方法
	 * 
	 * @return
	 */
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		// 下面配置Jackson2JsonMessageConverter来进行消息格式化,这种情况下,@RabbitListener标注的消费者方法需要使用Message进行统一接收
		ObjectMapper objectMapper = new ObjectMapper();
		// 配置转换是忽略NULL数据
		// 实体类与json互转的时候 属性值为null的不参与序列化
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
		// 设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
		rabbitTemplate.setMandatory(true);
		// 下面的回调函数设置也可以在具体调用时进行设置,因为本项目的rabbitTemplate设置成原型模式
		// 所以每个rabbitTemplate的回调都可以定制
		// 消息只要被 rabbitmq broker 接收到就会触发 confirmCallback 回调(其实是只要发送就会触发)
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				System.out.println("ConfirmCallback: 相关数据:    " + correlationData);
				System.out.println("ConfirmCallback: 确认情况:    " + ack);
				if (!ack) {
					// 如果沒有投递到broker 。。。
				}
				System.out.println("ConfirmCallback: 原因:            " + cause);
			}
		});

		// 当消息未投递到queue时的反馈(ReturnCallback)
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
					String routingKey) {
				System.out.println("ReturnCallback: 消息:           " + message);
				System.out.println("ReturnCallback: 回应码:       " + replyCode);
				System.out.println("ReturnCallback: 回应信息:    " + replyText);
				System.out.println("ReturnCallback: 交换机:        " + exchange);
				System.out.println("ReturnCallback: 路由键:        " + routingKey);
				// 逻辑处理。。。
			}
		});
		return rabbitTemplate;
	}
}
