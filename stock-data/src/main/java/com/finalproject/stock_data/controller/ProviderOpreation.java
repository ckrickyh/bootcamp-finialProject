package com.finalproject.stock_data.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalproject.stock_data.dto.HistoryDTO;
import com.finalproject.stock_data.dto.ProfileDTO;
import com.finalproject.stock_data.dto.ResponseDTO;

// localhost:8101
@RequestMapping("/us/")
public interface ProviderOpreation {
  
  @GetMapping(value = "/realtime")
  List<ResponseDTO> getFinnhub();

  @GetMapping(value = "/history/{usCode}") //redis needs JsonProcessingException
  List<HistoryDTO> getUsHistory(@PathVariable String usCode) throws JsonProcessingException; 

  @GetMapping(value = "/profile")
  List<ProfileDTO> getProfile();
}
