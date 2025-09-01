package com.finalproject.data_supplier.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalproject.data_supplier.dto.ApiProfileDTO;
import com.finalproject.data_supplier.dto.ResponseDTO;


// localhost:8100
public interface QuoteOperation {

  @GetMapping("/us/stocks")
  List<ResponseDTO> getUsStock() throws JsonProcessingException;

  @GetMapping("us/profile")
  List<ApiProfileDTO> getUsProfile();
}

