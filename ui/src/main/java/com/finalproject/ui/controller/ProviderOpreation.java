package com.finalproject.ui.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalproject.ui.dto.HistoryDTO;
import com.finalproject.ui.dto.ProfileDTO;
import com.finalproject.ui.dto.ResponseDTO;

// localhost:8102 because of docker
@RequestMapping("/us/")
public interface ProviderOpreation {
  
  @GetMapping(value = "/realtime")
  List<ResponseDTO> getBackEndRealTime();

  @GetMapping(value = "/history/{usCode}") 
  List<HistoryDTO> getBackEndUsHistory(@PathVariable String usCode) throws JsonProcessingException; 

  @GetMapping(value = "/profile")
  List<ProfileDTO> getBackEndProfile();
  
}
