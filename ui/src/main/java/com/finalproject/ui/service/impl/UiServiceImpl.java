package com.finalproject.ui.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.finalproject.ui.dto.HistoryDTO;
import com.finalproject.ui.dto.ProfileDTO;
import com.finalproject.ui.dto.ResponseDTO;
import com.finalproject.ui.service.UiService;

@Service
public class UiServiceImpl implements UiService {

  @Autowired
  RestTemplate restTemplate;

  @Override
  public List<ResponseDTO> getAllFinnhubData(){
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")

      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!
      .host("stock-data-app")
      // .host("localhost")
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!

      .port(8091)
      .path("us/realtime")
      .build()
      .toUriString();
      
    ResponseDTO[] response = this.restTemplate.getForObject(url, ResponseDTO[].class);

    if (response == null){
      return Collections.emptyList();
    } 
    List<ResponseDTO> responseDTOs = Arrays.asList(response);
  
    return responseDTOs;
  }

  @Override
  public List<ProfileDTO> getUsProfile(){
      String url = UriComponentsBuilder.newInstance()
      .scheme("http")

      // !!!!!!!!!!!!!!!!!!!!!!!!!!!
      .host("stock-data-app")
      // .host("localhost")
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!

      .port(8091)
      .path("us/profile")
      .build()
      .toUriString();
      
    ProfileDTO[] response = this.restTemplate.getForObject(url, ProfileDTO[].class);

    if (response == null){
      return Collections.emptyList();
    } 
    List<ProfileDTO> profileDTOs = Arrays.asList(response);
  
    return profileDTOs;
  }

  @Override
  public List<HistoryDTO> getBackEndUsHistory(String usCode) {
      String url = UriComponentsBuilder.newInstance()
      .scheme("http")

      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      .host("stock-data-app")
      // .host("localhost")
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    
      .port(8091)
      .path("us/history/" + usCode.toUpperCase())
      // .pathSegment(usCode)
      .build()
      .toUriString();
      
    HistoryDTO[] response = this.restTemplate.getForObject(url, HistoryDTO[].class);

    if (response == null){
      return Collections.emptyList();
    } 
    List<HistoryDTO> historyDTOs = Arrays.asList(response);
  
    return historyDTOs;
  }
  
}
