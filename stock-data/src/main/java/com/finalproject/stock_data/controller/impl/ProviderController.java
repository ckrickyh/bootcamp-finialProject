package com.finalproject.stock_data.controller.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalproject.stock_data.controller.ProviderOpreation;
import com.finalproject.stock_data.dto.HistoryDTO;
import com.finalproject.stock_data.dto.ProfileDTO;
import com.finalproject.stock_data.dto.ResponseDTO;
import com.finalproject.stock_data.entity.FinnhubEntity;
import com.finalproject.stock_data.mapper.HistoryDTOMapper;
import com.finalproject.stock_data.mapper.ProfileDTOMapper;
import com.finalproject.stock_data.mapper.ResponseDTOMapper;
import com.finalproject.stock_data.redis.service.RedisService;
import com.finalproject.stock_data.service.ProviderService;

@RestController
public class ProviderController implements ProviderOpreation{

  @Autowired
  ProviderService providerService;
  @Autowired
  ResponseDTOMapper responseDTOMapper;
  @Autowired
  HistoryDTOMapper historyDTOMapper;
  @Autowired
  ProfileDTOMapper profileDTOMapper;
  @Autowired
  RedisService redisService;


  @Override
  public List<ResponseDTO> getFinnhub(){
    List<FinnhubEntity> finnhubEntities = this.providerService.getAllFinnhubData();
    return finnhubEntities.stream().map(e -> this.responseDTOMapper.map(e))
      .collect(Collectors.toList());
  }

  // redis option
  @Override
  public List<HistoryDTO> getUsHistory(String usCode) throws JsonProcessingException {
    // ! without redis
    // List<HistoryDTO> historyDTOs = this.providerService.getUsHistory(usCode.toUpperCase()).stream()
    //   .map(e -> this.historyDTOMapper.map(e)).collect(Collectors.toList());
    // return historyDTOs;

    // ! with reids 
    
    List<HistoryDTO> historyDTOs = this.redisService.getCache(usCode).stream()
      .map(fnRedisDto -> this.historyDTOMapper.map(fnRedisDto))
      .collect((Collectors.toList()));
    return historyDTOs;
  }
  
  @Override
  public List<ProfileDTO> getProfile() {
    List<ProfileDTO> profileDTOs = this.providerService.getUsProfile().stream()
      .map(e -> this.profileDTOMapper.map(e)).collect(Collectors.toList());
    return profileDTOs;
  }

}
