package com.finalproject.ui.view.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Override
  public String loadStockTable(Model model) {
    List<ResponseDTO> stockData = this.uiService.getAllFinnhubData();
    model.addAttribute("stocks", stockData);
    return "heatmap";
  }

  @Override
  public String loadStockHistory(Model model, String usCode){
    List<HistoryDTO> stockHistory = this.uiService.getBackEndUsHistory(usCode);
    model.addAttribute("stockData", stockHistory);
    return "candlestick"; 
  }
}
