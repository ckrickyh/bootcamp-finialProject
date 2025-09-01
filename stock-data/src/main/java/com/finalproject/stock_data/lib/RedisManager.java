package com.finalproject.stock_data.lib;

import java.time.Duration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Example of Encapsulation
public class RedisManager {
  private RedisTemplate<String, String> redisTemplate;
  private ObjectMapper objectMapper;

  public RedisManager(RedisConnectionFactory factory,
      ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    RedisTemplate<String, String> rt = new RedisTemplate<>();
    rt.setConnectionFactory(factory);
    rt.setKeySerializer(RedisSerializer.string());
    rt.setValueSerializer(RedisSerializer.json());
    rt.afterPropertiesSet();
    this.redisTemplate = rt;
  }

  public <T> T read(String key, Class<T> clazz) throws RedisIOException {
    String json = this.redisTemplate.opsForValue().get(key);
    if (json == null)
      return null;
    try {
      return this.objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException ex) {
      throw new RedisIOException(ex.getMessage());
    }
  }

  public <T> void write(String key, T value, long timeoutInSecond)
      throws RedisIOException {
    String json;
    try {
      json = this.objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new RedisIOException(ex.getMessage());
    }
    this.redisTemplate.opsForValue() //
        .set(key, json, Duration.ofSeconds(timeoutInSecond));
  }
}
