package com.finalproject.ui.view;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// localhost:8102 for external 
// @RequestMapping("/us/")
public interface MainPageOperation {

  @GetMapping(value = "/us/heatmap")
  String loadStockTable(Model model);


  // localhost:8102/us/hisotrystick/NVDA => success
  @GetMapping(value = "/us/historystick/{usCode}")
  String loadStockHistory(Model model, @PathVariable String usCode);

}