# 开始

### 环境

1. jdk 11.0.6  JavaSE-11

### 打包

1. 使用maven的打包命令即可 `clean package`

### 引入

1. 在需要引入的maven工程中的`pom.xml`文件中引入即可

   ```xml
   <dependency>
   	<groupId>cn.t.serialization.util</groupId>
   	<artifactId>serialization-util</artifactId>
   	<version>1.0</version>
   	<scope>system</scope>
   	<systemPath>${basedir}/lib/serialization-util-1.0.jar</systemPath>
   </dependency>
   ```

   

