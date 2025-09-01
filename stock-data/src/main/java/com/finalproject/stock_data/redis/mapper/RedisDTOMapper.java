package com.finalproject.stock_data.redis.mapper;

import org.springframework.stereotype.Component;
import com.finalproject.stock_data.entity.HistoryEntity;
import com.finalproject.stock_data.redis.FrontEndRedisHistoryDTO;
import com.finalproject.stock_data.redis.RedisHistoryDTO;

@Component
public class RedisDTOMapper {
  
  public FrontEndRedisHistoryDTO map(RedisHistoryDTO redisHistoryDTO) {
    return FrontEndRedisHistoryDTO.builder() //
        // .index(Integer.parseInt(redisHistoryDTO.getIndex()))
        .index(redisHistoryDTO.getIndex())
        .code(redisHistoryDTO.getCode())
        .close(redisHistoryDTO.getClose())
        .open(redisHistoryDTO.getOpen())
        .volume((redisHistoryDTO.getVolume()))
        .high(redisHistoryDTO.getHigh())
        .low(redisHistoryDTO.getLow())
        .symbolLink(redisHistoryDTO.getSymbolLink())
        .date(redisHistoryDTO.getDate())
        .build();
  }

  public FrontEndRedisHistoryDTO map(HistoryEntity historyEntity) {
    return FrontEndRedisHistoryDTO.builder() //
        .index(historyEntity.getIndex())
        // .index(Integer.parseInt(historyEntity.getIndex()))
        .code(historyEntity.getCode())
        .close(historyEntity.getClose())
        .open(historyEntity.getOpen())
        .volume((historyEntity.getVolume()))
        .high(historyEntity.getHigh())
        .low(historyEntity.getLow())
        .symbolLink(historyEntity.getSymbolLink())
        .date(historyEntity.getDate())
        .build();
  }

  public RedisHistoryDTO mapDB(HistoryEntity historyEntity) {
    return RedisHistoryDTO.builder() //
        .index(historyEntity.getIndex())
        // .index(Integer.parseInt(historyEntity.getIndex()))
        .code(historyEntity.getCode())
        .close(historyEntity.getClose())
        .open(historyEntity.getOpen())
        .volume((historyEntity.getVolume()))
        .high(historyEntity.getHigh())
        .low(historyEntity.getLow())
        .symbolLink(historyEntity.getSymbolLink())
        .date(historyEntity.getDate())
        .build();
  }
}
