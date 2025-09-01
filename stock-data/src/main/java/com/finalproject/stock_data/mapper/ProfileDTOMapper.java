package com.finalproject.stock_data.mapper;

import org.springframework.stereotype.Component;
import com.finalproject.stock_data.dto.ProfileDTO;
import com.finalproject.stock_data.entity.ProfileEntity;

@Component
public class ProfileDTOMapper {
  public ProfileDTO map(ProfileEntity profileEntity) {
    return ProfileDTO.builder()
      .ticker(profileEntity.getCode())
      .marketCapitalization(profileEntity.getMarketCapitalization())
      .country(profileEntity.getCountry())
      .currency(profileEntity.getCurrency())
      .estimateCurrency(profileEntity.getEstimateCurrency())
      .exchange(profileEntity.getExchange())
      .finnhubIndustry(profileEntity.getFinnhubIndustry())
      .ipo(profileEntity.getIpo())
      .logo(profileEntity.getLogo())
      .name(profileEntity.getName())
      .phone(profileEntity.getPhone())
      .shareOutstanding(profileEntity.getShareOutstanding())
      .weburl(profileEntity.getWeburl())
      .build();
  }
}
