package com.finalproject.ui.controller.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalproject.ui.controller.ProviderOpreation;
import com.finalproject.ui.dto.HistoryDTO;
import com.finalproject.ui.dto.ProfileDTO;
import com.finalproject.ui.dto.ResponseDTO;
import com.finalproject.ui.service.UiService;


@RestController
public class ProviderController implements ProviderOpreation{

  @Autowired
  UiService uiService;

  @Override
  public List<ResponseDTO> getBackEndRealTime(){
    List<ResponseDTO> responseDTOs = this.uiService.getAllFinnhubData();
    return responseDTOs;
  }

  @Override
  public List<HistoryDTO> getBackEndUsHistory(String usCode) throws JsonProcessingException {
    List<HistoryDTO> historyDTOs = this.uiService.getBackEndUsHistory(usCode.toUpperCase());
    return historyDTOs;
  }
  
  @Override
  public List<ProfileDTO> getBackEndProfile(){
    List<ProfileDTO> profileDTOs = this.uiService.getUsProfile();
    return profileDTOs;
  }

}
