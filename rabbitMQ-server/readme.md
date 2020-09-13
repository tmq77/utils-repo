# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.10.BUILD-SNAPSHOT/maven-plugin/)
* [Spring for RabbitMQ](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#boot-features-amqp)

### Guides
The following guides illustrate how to use some features concretely:

* [Messaging with RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)

### 环境

`spring-boot-starter-parent`2.2.10.BUILD-SNAPSHOT

`spring-boot-starter-amqp`2.2.10.BUILD-SNAPSHOT

`JDK` 11.0.6

`ErLang`23

`RabbitMQ`3.8.7

### 配置RabbitMQ消息生产者

`RabbitMQConfig.java`配置类中声明了`RabbitMQ`的**队列、交换机以及对应的绑定关系**

配置类中还声明了`ConnectionFactory`的`Bean`，用于配置`RabbitTemplate`的回调方法以及`MessageConverter`。但实际开发可能不需要这么复杂,只要在对应的`yml`文件中配置即可,

 `MessageConverter` 使用了`springBoot-amqp`提供的消息转换类`Jackson2JsonMessageConverter`,实际开发中可能不需要配置这个转换器，在消费者方法里面直接调用它的`fromMessage`方法即可完成`byte[]`=>`Object`的转换了。

回调方法可以在配置类以外进行配置使用：`rabbitTemplate.setConfirmCallback(org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback)`以及`rabbitTemplate.setReturnCallback(org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback)`即可,方法中传入对应接口的实现类即可完成回调的配置。

>  注意点: 开启回调后，`ConfirmCallback`在消息发送后就会触发,`ReturnCallback`只有在消息达到交换机后,未成功送达队列时触发,`ReturnCallback`不能作为消息被成功消费的标志。

### 配置RabbitMQ消息消费者

消费者只需要引入依赖然后配置相关消费逻辑即可

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

消费者类的构成

```java
/**
 * 注入Channel后可以进行手动确认,这个方法能够正常运行的前提是yml文件配置了手动确认，
 * 否则出现异常重入队列时会抛出channel关闭的异常(自动确认下通信完成就会关闭通道)
 * 配置项:
 * spring.rabbitmq.listener.simple.acknowledge-mode = MANUAL
 * @param message 消息体
 * @param channel 通道
 */
@RabbitListener(queues = "topic.queue.t1")  // 监听的队列
public void process(Message message, Channel channel) {
		try {
			System.out.println("消费者收到消息  : " + message);
			// Jackson2JsonMessageConverter这个转换器是SpringBoot-amqp提供的消息转换类
			System.out.println("转换回原来的对象:" + new Jackson2JsonMessageConverter().fromMessage(message));
			System.out
					.println("转换回原来的对象并取出属性:" + ((User) new Jackson2JsonMessageConverter().fromMessage(message)).getName());
			// 手动确认，第二个参数指代本次确认只确认当前信息,避免线程问题
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			e.printStackTrace();
			if (message.getMessageProperties().isRedelivered()) {
				System.out.println("消息被重新投递");
			}
			try {
         // 拒绝该消息,第二个参数表示是否重新进入队列 =>拒绝后如果配置了死信则进入死信队列 
		channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
```

`spring-boot-starter-amqp`中提供了`Jackson2JsonMessageConverter`用于对象转换,使用它的`fromMessage(Message)`方法可以直接从消息体中取出实体内容。

在`Message`中可以通过取得队列名来分支对应处理不同的队列业务逻辑`message.getMessageProperties().getConsumerQueue()`可以获得当前消息所属队列。

在接收消息过程中出现异常时,可以手动将消息转入死信队列然后后续处理，也可以将消息存入`Redis`等数据库进行持久化,然后统一在定时任务里根据存储的队列名等信息进行重新发送。

如果没有开启手动确认,那么可以使用重试机制,重试时异常时自动触发,如果`try-catch`处理后则不会触发重试,程序可能出现死循环。超过重试次数的处理情况则根据配置执行。

一般使用了重试就不会进入死信队列了

> 当`channel.basicNack` 第三个参数设为false时，消息签收失败,此时消息进入死信队列，完成消费(需要配置死信队列)

### 死信队列配置

```java
/**
 * 创建正常的业务队列以及绑定死信交换机信息以及死信路由键
 */
@Bean
public Queue topicQueue() {
	// 配置死信队列需要先将正常队列与死信交换机建立关系
	return QueueBuilder.durable(this.mqProperties.getTopicQueue()) // 配置队列(持久化)
	// 配置死信交换机名
	.withArgument("x-dead-letter-exchange",this.mqProperties.getDeadExchange())
	.withArgument("x-dead-letter-routing-key",this.mqProperties.getDeadQueue())
	.withArgument("x-message-ttl", 20000) // 消息过期时间20S,超时消息进入死信(实际测试并没有,channel不关闭就一直在业务队列)
				.build();
	}
```

死信队列需要依赖于正常的业务队列,所以在正常的业务队列上添加死信交换机的参数即可,然后创建专门的死信队列存放死信:

```java
/**
 * 死信队列，正常业务队列中的异常消息会根据正常队列的死信绑定信息转到这里
 * 出现死信时,正常业务队列会把死信给到死信交换机，再由死信交换机投递到这里
 * @return
 */
 @Bean
 public Queue deadQueue() {
	return new Queue(this.mqProperties.getDeadQueue());
 }
```

将正常业务队列绑定到正常的业务交换机上

```java
/**
 * 创建绑定 将业务队列绑定至交换机并绑定路由键
 * 将配置了死信的业务队列绑定到正常业务交换机
 * 正常的业务交换机会根据正常的路由Key发送消息到正常的业务队列
 */
 @Bean
 public Binding bindingExchange() {
	 return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(this.mqProperties.getTopicQueue());
 }
```

由于正常的业务队列上有死信交换机名以及死信的路由键,所以出现死信的时候会自动转发到死信队列,下面将死信队列与死信交换机绑定即可,注意路由键需要与正常队列一致,这样正常队列产生的死信会通过死信路由键传递到死信交换机，然后死信交换机会把死信传递到绑定了死信路由键的死信队列上。

死信交换机与死信绑定的创建：

```java
/**
 * 死信交换机 死信队列跟交换机类型没有关系 不一定为DirectExchange 不影响该类型交换机的特性
 */
 @Bean
 public DirectExchange deadExchange() {
	 // 死信交换机也是一个普通的交换机
	 return new DirectExchange(this.mqProperties.getDeadExchange());
 }

/**
 * 创建绑定,将死信接收队列绑定到死信交换机上
 */
 @Bean
 public Binding deadQueueBinding() {
	 return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(this.mqProperties.getDeadQueue());
 }
```

至此,死信配置完成。客户端专门监听死信队列即可做相应处理。



