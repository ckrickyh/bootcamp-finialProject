package com.finalproject.ui.view.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.finalproject.ui.dto.HistoryDTO;
import com.finalproject.ui.dto.ResponseDTO;
import com.finalproject.ui.service.UiService;
import com.finalproject.ui.view.MainPageOperation;

@Controller // ! return html
public class MainPageController implements MainPageOperation {
  @Autowired
  UiService uiService;

  @Value("${BASE_URL:}") // Optional explicit override
  private String baseUrl;

  @Value("${APP_ENV:local}") // Identify deployment environment (local | gcp | etc.)
  private String appEnv;

  @Override
  public String loadStockTable(Model model) {
    List<ResponseDTO> stockData = this.uiService.getAllFinnhubData();
    model.addAttribute("stocks", stockData);
    model.addAttribute("baseUrl", resolveBaseUrl());
    return "heatmap";
  }

  @Override
  public String loadStockHistory(Model model, String usCode){
    List<HistoryDTO> stockHistory = this.uiService.getBackEndUsHistory(usCode);
    model.addAttribute("stockData", stockHistory);
    return "candlestick"; 
  }

  private String resolveBaseUrl() {
    if (baseUrl != null && !baseUrl.trim().isEmpty()) {
      return baseUrl.trim();
    }

    if ("gcp".equalsIgnoreCase(appEnv)) {
      return "https://natureai.dpdns.org";
    }

    // Default for local development
    return "http://localhost:8102";
  }
}
