package com.finalproject.stock_data.mapper;

import org.springframework.stereotype.Component;
import com.finalproject.stock_data.dto.ProfileDTO;
import com.finalproject.stock_data.entity.ProfileEntity;

@Component
public class ProfileEntityMapper {
  public ProfileEntity map(ProfileDTO profileDTO) {
    return ProfileEntity.builder()
      .code(profileDTO.getTicker())
      .marketCapitalization(profileDTO.getMarketCapitalization())
      .country(profileDTO.getCountry())
      .currency(profileDTO.getCurrency())
      .estimateCurrency(profileDTO.getEstimateCurrency())
      .exchange(profileDTO.getExchange())
      .finnhubIndustry(profileDTO.getFinnhubIndustry())
      .ipo(profileDTO.getIpo())
      .logo(profileDTO.getLogo())
      .name(profileDTO.getName())
      .phone(profileDTO.getPhone())
      .shareOutstanding(profileDTO.getShareOutstanding())
      .weburl(profileDTO.getWeburl())
      .build();
  }
}
