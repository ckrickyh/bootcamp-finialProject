package com.finalproject.stock_data.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.finalproject.stock_data.dto.FinnhubDTO;
import com.finalproject.stock_data.dto.ProfileDTO;
import com.finalproject.stock_data.entity.FinnhubEntity;
import com.finalproject.stock_data.entity.HistoryEntity;
import com.finalproject.stock_data.entity.ProfileEntity;
import com.finalproject.stock_data.mapper.FinnhubEntityMapper;
import com.finalproject.stock_data.mapper.ProfileEntityMapper;
import com.finalproject.stock_data.mapper.ResponseDTOMapper;
import com.finalproject.stock_data.repository.FinnhubRepository;
import com.finalproject.stock_data.repository.HistoryRepository;
import com.finalproject.stock_data.repository.ProfileRepository;
import com.finalproject.stock_data.service.ProviderService;

@Service
public class ProviderServiceImpl implements ProviderService{

  @Autowired
  RestTemplate restTemplate;
  @Autowired
  FinnhubEntityMapper ohlcEntityMapper;
  @Autowired
  HistoryRepository historyRepository;
  @Autowired
  ProfileEntityMapper profileEntityMapper;
  @Autowired
  ProfileRepository profileRepository;
  @Autowired
  ResponseDTOMapper responseDTOMapper;
  @Autowired
  FinnhubEntityMapper finnhubEntityMapper;
  @Autowired
  FinnhubRepository finnhubRepository;
  // @Autowired
  // ObjectMapper objectMapper;

  @Override
  public List<FinnhubEntity> getAllFinnhubData(){
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")

      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      .host("data-supplier-app")               // Docker
      // .host("localhost")                          // localhost
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

      .port(8090)                               //對內
      .path("us/stocks")
      .build()
      .toUriString();
    // String url = "http://localhost:8090/us/stocks"; //localhost
    System.out.println("rgbesrbdvfafaad" + url);

    FinnhubDTO[] response =  this.restTemplate.getForObject(url, FinnhubDTO[].class);
    
    if (response == null){
      return Collections.emptyList();
    } 

    List<FinnhubDTO> finnhubDTOs = Arrays.asList(response);

    //mapper from dto(d) to entity, include profileEntity(p) by findByCode d.getCode <- fetchProfileEntity
    List<FinnhubEntity> finnhubEntities = finnhubDTOs.stream()
      .map(d -> {
          ProfileEntity profileEntity = fetchProfileEntity(d.getCode());
          return this.finnhubEntityMapper.map(d, profileEntity); // this return is fetchProfileEntity inside lambda
      })
      .collect(Collectors.toList());

    return finnhubEntities;
  }

  // !!!
  @Override
  public ProfileEntity fetchProfileEntity(String profileStockCode) {
    return this.profileRepository.findById(profileStockCode)
      .orElseThrow(() -> new RuntimeException("Profile not found for code: " + profileStockCode));
  }

  @Override
  public void saveFinnhubData(FinnhubDTO finnhubDTO) {
      // Fetch the ProfileEntity based on the profileStockCode
      ProfileEntity profileEntity = profileRepository.findById(finnhubDTO.getCode())
              .orElseThrow(() -> new RuntimeException("Profile not found"));

      FinnhubEntity entity = this.finnhubEntityMapper.map(finnhubDTO, profileEntity);
      this.finnhubRepository.save(entity);
  }

  @Override
  public List<FinnhubEntity> saveAll(List<FinnhubEntity> ohlcEntities){
    return this.finnhubRepository.saveAll(ohlcEntities);
  }

  @Override
  public void deleteAllOHLCs(){
      this.finnhubRepository.deleteAll();
  }
  
  // ==== History ====
  @Override
  public List<HistoryEntity> getUsHistory(String usCode){
    return this.historyRepository.findByCode(usCode);
  }

  // ===== Profile ====
  @Override
  public List<ProfileEntity> getUsProfile(){
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")

      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      .host("data-supplier-app")         //  Docker
      // .host("localhost")              //  localhost
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

      .port(8090)                       //對內
      .path("us/profile")
      .build()
      .toUriString();
    // String url = "http://localhost:8090/us/profile";

    ProfileDTO[] response =  this.restTemplate.getForObject(url, ProfileDTO[].class);
    
    if (response == null){
      return Collections.emptyList();
    } 
    
    List<ProfileDTO> profileDTOs = Arrays.asList(response);

    // change dto to entity
    List<ProfileEntity> profileEntities = profileDTOs.stream().map(dto -> this.profileEntityMapper.map(dto))
      .collect(Collectors.toList());

    return profileEntities;
  }
  
  @Override
  public List<ProfileEntity> saveAllProfiles(List<ProfileEntity> profileEntities){
    return this.profileRepository.saveAll(profileEntities);
  }

  @Override
  public void deleteAllProfiles(){
    this.profileRepository.deleteAll();
  }
}
