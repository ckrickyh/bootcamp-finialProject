package com.finalproject.stock_data.mapper;

import org.springframework.stereotype.Component;
import com.finalproject.stock_data.dto.HistoryDTO;
import com.finalproject.stock_data.entity.HistoryEntity;
import com.finalproject.stock_data.redis.FrontEndRedisHistoryDTO;

@Component
public class HistoryDTOMapper{

  public HistoryDTO map(HistoryEntity historyEntity){
    return HistoryDTO.builder()
      .index(historyEntity.getIndex())
      // .index(Integer.parseInt(historyEntity.getIndex()))
      .code(historyEntity.getCode())
      .close(historyEntity.getClose())
      .open(historyEntity.getOpen())
      .volume(historyEntity.getVolume())
      .high(historyEntity.getHigh())
      .low(historyEntity.getLow())
      .symbolLink(historyEntity.getSymbolLink())
      .date(historyEntity.getDate())
      // .date(historyEntity.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
      .build();
  }

  public HistoryDTO map(FrontEndRedisHistoryDTO frontEndRedisHistoryDTO){
    return HistoryDTO.builder()
      .index(frontEndRedisHistoryDTO.getIndex())
      .code(frontEndRedisHistoryDTO.getCode())
      .close(frontEndRedisHistoryDTO.getClose())
      .open(frontEndRedisHistoryDTO.getOpen())
      .volume(frontEndRedisHistoryDTO.getVolume())
      .high(frontEndRedisHistoryDTO.getHigh())
      .low(frontEndRedisHistoryDTO.getLow())
      .symbolLink(frontEndRedisHistoryDTO.getSymbolLink()) 
      .date(frontEndRedisHistoryDTO.getDate())
      // .date(frontEndRedisHistoryDTO.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) // redisHistoryDTO is String
      .build();
  }

}
