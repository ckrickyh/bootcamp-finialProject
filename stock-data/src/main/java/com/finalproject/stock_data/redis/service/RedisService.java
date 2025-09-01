package com.finalproject.stock_data.redis.service;

import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalproject.stock_data.redis.FrontEndRedisHistoryDTO;

public interface RedisService {

  List<FrontEndRedisHistoryDTO> getCache(String targetCode) throws JsonProcessingException;
}
 