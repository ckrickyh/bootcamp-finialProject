package com.finalproject.stock_data.mapper;

import org.springframework.stereotype.Component;
import com.finalproject.stock_data.dto.FinnhubDTO;
import com.finalproject.stock_data.entity.FinnhubEntity;
import com.finalproject.stock_data.entity.ProfileEntity;

@Component
public class FinnhubEntityMapper {
  
  public FinnhubEntity map(FinnhubDTO finnhubDTO, ProfileEntity profileEntity){
    return FinnhubEntity.builder()
      .code(finnhubDTO.getCode())
      .currentPrice(finnhubDTO.getCurrentPrice())
      .changePrice(finnhubDTO.getChangePrice())
      .percentageChange(finnhubDTO.getPercentageChange())
      .high(finnhubDTO.getHigh())
      .low(finnhubDTO.getLow())
      .open(finnhubDTO.getOpen())
      .previousClose(finnhubDTO.getPreviousClose())
      .timestamp(finnhubDTO.getTimestamp())
      .profileEntity(profileEntity)
      .build();

  }
}
