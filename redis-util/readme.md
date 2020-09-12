# 项目说明

#### 项目环境

- jdk11.0.6
- redis6.0.7
- springBoot2.2.10
- spring-boot-starter-data-redis2.2.10
- lombok1.18.12
- log4j2

---

#### 项目配置说明

- Spring2.x.x的默认redis客户端用的是lettuce,但是本项目使用的是jedis,所以需要在配置中将客户端配置成jedis。
- `RedisConfig`类中做了全部的配置工作
  - 创建连接工厂
    - 配置连接池
    - 配置连接信息
  - 创建`RedisTemplate`
    - 配置解析器
- `ConfigProperties`类中通过读取配置文件进行配置对象的设置
- `RedisUtil`类中封装了一些常用的API，但未全部进行测试
- 项目类路径下已经配置了`spring.factories`文件,指定到了本项目的配置类路径,使用`maven install`方式打包时,直接引用本包即可实现配置

---

#### 如何引入这个工具包

1. 第一种方式

    源码中的pom.xml文件中的打包插件需要换成apache的,如果使用默认的springboot的打包插件会导致打的jar包即使被其他项目依赖,包中的class也无法被`import`,springboot打包后第一层级是`BOOT-INF`。

    最终包含依赖的jar包名应该是`redis-util-1.0-jar-with-dependencies.jar`这种格式。
    
    打包时同时把源码和javaDoc一并打包。
    
    在工程的Maven library中找到RedisUtil这个包,然后右键properties,弹出窗口中找到`JavaDoc location`,在里面指定解压完的javaDoc文件夹即可在项目中看到工具包的注释。
    
    查看源码的过程同上找到`Java Source Attachment`指定即可。
    
    上述查看源码和注释的相关内容可以网上检索关键字`java attach source`。

    打包时的pom配置参考项目中的pom.xml的plugins节点。
    
    在需要使用的项目的pom.xml文件中加入以下依赖即可
    
    ```xml
    <dependency>
         <groupId>cn.t.redis.util</groupId>
     	 <artifactId>redis-util</artifactId>
         <version>1.0</version>
         <scope>system</scope>
         <systemPath>${basedir}/lib/redis-util-1.0.jar</systemPath>
    </dependency>
    ```
    
    > 不要忘记将jar包放入lib目录下
    >
    > 注意groupId尽量和源码的地址一样
    >
    > 注意打包时需要将依赖一并打入,具体配置查看pom.xml的plugins节点
    
    - 在boot启动类的注解上指定包扫描路径,将引入的包路径加入进去,否则工具类无法初始化
    
      ```java
      @SpringBootApplication(scanBasePackages = {"当前项目扫描路 径","cn.t.redis(工具包路径)"})
      ```
    
    或者
    
    - 在需要引入这个包的工程类路径(`resources`目录)下新建`spring.factories`文件,并且加入如下代码,因为`srpingBoot`启动时会去加载`spring.factories`文件中的信息（具体参考SrpingBoot是如何自动配置的）,下面的代码配置了`@EnableAutoConfiguration`注解所需要的值,那么Spring会自动把对应路径的java类配置成`Bean`。
    
      > 包含依赖一并打包的时候会把本项目的`spring.factories`文件覆盖,所以需要自己手动添加,但是这样做可能导致依赖本项目的工程打包时对应文件被覆盖的风险
    
      ```
      org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
      cn.t.redis.util.config.RedisConfig
      ```
    
2. 第二种方式

    源码打包时使用默认的springboot的打包插件,使用install命令将jar包安装到本地仓库下,然后在项目中引用即可

    ```xml
    <dependency>
         <groupId>cn.t.redis.util</groupId>
         <artifactId>redis-util</artifactId>
         <version>1.0</version>
    </dependency>
    ```

---

#### 如何配置这个工具包

如同普通的SpringBoot项目配置redis一样,相关的Redis连接配置信息使用默认的Spring配置键来配置即可,工具包中已配置了几项基本的配置，其余配置全部使用默认值。

目前能够自定义更改的9项配置如下:

```yaml
spring:
  redis:
    host: localhost
    port: 18379
    password: root
    timeout: 1000
    database: 0 #数据库索引,使用哪个数据库
    jedis:
      pool:
        max-active: 10 #连接池最大连接数（使用负值表示没有限制）   默认8
        max-wait: 1500 #连接池最大阻塞等待时间（使用负值表示没有限制）   默认-1
        max-idle: 8 #连接池中的最大空闲连接     默认8
        min-idle: 0 #连接池中的最小空闲连接     默认0
```

