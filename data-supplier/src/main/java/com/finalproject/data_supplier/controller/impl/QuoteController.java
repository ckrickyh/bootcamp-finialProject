package com.finalproject.data_supplier.controller.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.finalproject.data_supplier.controller.QuoteOperation;
import com.finalproject.data_supplier.dto.ApiProfileDTO;
import com.finalproject.data_supplier.dto.ResponseDTO;
import com.finalproject.data_supplier.service.QuoteService;
import com.finalproject.data_supplier.stockList.StockList;

@RestController
public class QuoteController implements QuoteOperation {
  
  @Autowired
  private QuoteService quoteService;

  // ====== counter api
  // private final List<String> apiKeys = List.of("API_KEY_1", "API_KEY_2", "API_KEY_3");
  // private int currentApiKeyIndex = 0;
  // ======

  @Override
  public List<ResponseDTO> getUsStock() throws JsonProcessingException{

    // String apiKey = apiKeys.get(currentApiKeyIndex);
    // // Fetch data using the current API key
    // List<Stock> usCodesstocks = fetchStocks(apiKey);
    // // Update the counter api
    // currentApiKeyIndex++;
    // if (currentApiKeyIndex >= apiKeys.size()) {
    //     currentApiKeyIndex = 0; // Reset to the first key
    // }
    // List<String> usCodes = StockList.getListByCsv("/Users/hochakkong/Documents/github/finalproject/stock-data/src/main/java/com/finalproject/stock_data/stockList/StockList.csv")

    //==============
    List<String> usCodes = StockList.getList();
    System.out.println(usCodes);
    return quoteService.fetchData(usCodes);
    //==============


    // return quoteService.fetchData(usCodes, apiKey);
  }

  @Override
  public List<ApiProfileDTO> getUsProfile(){
    List<String> usCodes = StockList.getList();
    // List<String> usCodes = StockList.getListByCsv("/Users/hochakkong/Documents/github/finalproject/stock-data/src/main/java/com/finalproject/stock_data/stockList/StockList.csv")
    return quoteService.fetchProfileData(usCodes);
  }
}
