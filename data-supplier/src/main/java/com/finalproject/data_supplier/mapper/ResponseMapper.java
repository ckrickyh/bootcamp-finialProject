package com.finalproject.data_supplier.mapper;

import org.springframework.stereotype.Component;
import com.finalproject.data_supplier.dto.ApiDTO;
import com.finalproject.data_supplier.dto.ResponseDTO;

@Component
public class ResponseMapper {
  
  public ResponseDTO map(ApiDTO apiDTO, String usCode){
    return ResponseDTO.builder()
      .code(usCode)
      .percentageChange(apiDTO.getPercentageChange())
      .open(apiDTO.getOpen())
      .previousClose(apiDTO.getPreviousClose())
      .high(apiDTO.getHigh())
      .low(apiDTO.getLow())
      .changePrice(apiDTO.getChangePrice())
      .currentPrice((apiDTO.getCurrentPrice()))
      .timestamp(apiDTO.getTimestamp())
      .build();
  }
  
}
