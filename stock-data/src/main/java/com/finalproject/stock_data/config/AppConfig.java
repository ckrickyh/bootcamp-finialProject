package com.finalproject.stock_data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finalproject.stock_data.lib.RedisManager;

@Configuration
public class AppConfig {
  
  @Bean
  RestTemplate restTemplate(){
    return new RestTemplate();
  }

  // ==== Redis ====
  @Bean
  RedisManager redisManager(RedisConnectionFactory factory,
      ObjectMapper objectMapper) {
      return new RedisManager(factory, objectMapper);
  }

  // pom.xml -> dependency -> class file
  // get key -> return value (similar hashmap)

  // ! The bean of RedisTemplate depends on RedisConnectionFactory and
  // ObjectMapper
  // You spring context by default contains the bean of RedisConnectionFactory
  // But objectmapper need to be created by yourself
  @Bean
  RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
      RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(factory);
      redisTemplate.setKeySerializer(RedisSerializer.string());
      redisTemplate.setValueSerializer(RedisSerializer.json());
      redisTemplate.afterPropertiesSet();
      return redisTemplate;
  }

  @Bean
  ObjectMapper objectMapper() {
      // json 以文字顯示
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      return mapper;
      // return new ObjectMapper();
  }
  //==== redis end ====
}
