package com.finalproject.data_supplier.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.finalproject.data_supplier.dto.ApiDTO;
import com.finalproject.data_supplier.dto.ApiProfileDTO;
import com.finalproject.data_supplier.dto.ResponseDTO;
import com.finalproject.data_supplier.mapper.ResponseMapper;
import com.finalproject.data_supplier.service.QuoteService;
import jakarta.annotation.PostConstruct;


@Service
public class QuoteServiceImpl implements QuoteService{
  
  @Autowired
  RestTemplate restTemplate;
  @Autowired 
  ResponseMapper responseMapper;
  @Value("${service.finnhub.host}")
  private String host;
  @Value("${service.finnhub.api.quote.key-value}")
  private String keyValue;
  @Value("${service.finnhub.api.quote.key-value2}")
  private String keyValue2;
  @Value("${service.finnhub.api.quote.key-value3}")
  private String keyValue3;
  @Value("${service.finnhub.api.quote.key-value4}")
  private String keyValue4;
  @Value("${service.finnhub.api.quote.endpoint}")
  private String endPoint;
  @Value("${service.finnhub.api.quote.version}")
  private String version;

  // live data
  private String[] apiKeysLive;
  @PostConstruct
  public void init() {
      this.apiKeysLive = new String[]{keyValue, keyValue2, keyValue3, keyValue4};
  }
  //https://finnhub.io/api/v1/quote?symbol=AAPL&token=key-value
 
  // historial data
  private static final String keyValueH1 = "d1utrt1r01qp42gvlo90d1utrt1r01qp42gvlo9g";
  private static final String keyValueH2 = "d2qo349r01qn21mkcoi0d2qo349r01qn21mkcoig";
  private static final String keyValueH3 = "d2qse89r01qluccprutgd2qse89r01qluccpruu0";
  private static final String keyValueH4 = "d2qskahr01qluccpsmk0d2qskahr01qluccpsmkg";
  String[] apiKeysHistory = {keyValueH1, keyValueH2, keyValueH3, keyValueH4};
  private static final String Profile_URL = "https://finnhub.io/api/v1/stock/profile2";
  //https://finnhub.io/api/v1/stock/profile2?symbol=AAPL&token=key-value

  @Override
  public List<ResponseDTO> fetchData(List<String> usCodes) {
    
    int x = 0;
    // String apiKey = keyValue;

    List<ApiDTO> apiDTOs = new ArrayList<>();
    for (String usCode : usCodes) {
      // String apiUrl = BASE_URL + "quote?symbol=" + usCode + "&token=" + API_KEY;  
      String apiKey = getApiKeyForIndex(x, apiKeysLive);
      
      String apiUrl = UriComponentsBuilder.newInstance() //
        .scheme("https") //
        .host(host) //
        .pathSegment(version) //
        .path(endPoint)
        .queryParam("symbol", usCode) //
        .queryParam("token", apiKey) //
        .build() //
        .toUriString();
      System.out.println("url=" + apiUrl);

      ApiDTO apiDTO = this.restTemplate.getForObject(apiUrl, ApiDTO.class);
      if (apiDTO != null) {
        apiDTOs.add(apiDTO);
      }
      x++;
    } 

    // Map ApiDTOs to ResponseDTOs with index
    List<ResponseDTO> responseDTOList = new ArrayList<>();
    for (int i = 0; i < apiDTOs.size(); i++) {
        ApiDTO apiDTO = apiDTOs.get(i);
        ResponseDTO responseDTO = responseMapper.map(apiDTO, usCodes.get(i));
        responseDTO.setCode(usCodes.get(i)); // Set the index
        if (responseDTO != null) {
            responseDTOList.add(responseDTO);
        }
    }
    return responseDTOList; // Return the final list of ResponseDTOs

    // Alternative way to map ApiDTOs to ResponseDTOs using streams
    // return apiDTOs.stream()
    //   .map(apiDTO -> this.responseMapper.map(apiDTO, usCodes.get(apiDTOs.indexOf(apiDTO)))) // Using index to get stock code
    //   .filter(apiDTO -> apiDTO!=null).collect(Collectors.toList());
  }

  @Override
  public List<ApiProfileDTO> fetchProfileData(List<String> usCodes){
    List<ApiProfileDTO> apiProfileDTOs = new ArrayList<>();
    
    // String apiKey = keyValue3;
    int x = 0;

    for (String usCode : usCodes) {

      String apiKey = getApiKeyForIndex(x, apiKeysHistory);
      String apiUrl = Profile_URL + "?symbol=" + usCode + "&token=" + apiKey;
      System.out.println(apiUrl);
      ApiProfileDTO apiProfileDTO = this.restTemplate.getForObject(apiUrl, ApiProfileDTO.class);
      if (apiProfileDTO != null) {
        apiProfileDTOs.add(apiProfileDTO);
      }

       x++;
    }
   
    return apiProfileDTOs;
  }
  
  private static String getApiKeyForIndex(int index, String[] apiKeys) {
      return apiKeys[(index / 12) % apiKeys.length];
  }

}