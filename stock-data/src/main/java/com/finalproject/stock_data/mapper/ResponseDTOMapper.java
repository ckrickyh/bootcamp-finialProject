package com.finalproject.stock_data.mapper;

import org.springframework.stereotype.Component;
import com.finalproject.stock_data.dto.ResponseDTO;
import com.finalproject.stock_data.entity.FinnhubEntity;

@Component
public class ResponseDTOMapper {

  public ResponseDTO map(FinnhubEntity finnhubEntity){
    return ResponseDTO.builder()
      // .id(finnhubEntity.getId())
      .code(finnhubEntity.getCode())
      .currentPrice(finnhubEntity.getCurrentPrice())
      .changePrice(finnhubEntity.getChangePrice())
      .percentageChange(finnhubEntity.getPercentageChange())
      .high(finnhubEntity.getHigh())
      .low(finnhubEntity.getLow())
      .open(finnhubEntity.getOpen())
      .previousClose(finnhubEntity.getPreviousClose())
      .timestamp(finnhubEntity.getTimestamp())
      .finnhubIndustry(finnhubEntity.getProfileEntity() != null ? finnhubEntity.getProfileEntity().getFinnhubIndustry() : null)
      .build();
  }
}
