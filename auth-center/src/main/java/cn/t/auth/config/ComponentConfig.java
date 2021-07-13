package cn.t.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class ComponentConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    //在反序列化时忽略在 json 中存在但 Java 对象不存在的属性 
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
    // null对象仍可以序列化
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    //在序列化时忽略值为 null 的属性 
    objectMapper.setSerializationInclusion(Include.NON_NULL); 
    return objectMapper;
  }
}
