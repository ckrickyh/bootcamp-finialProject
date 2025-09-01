package com.finalproject.data_supplier.service;

import java.util.List;
import com.finalproject.data_supplier.dto.ApiProfileDTO;
import com.finalproject.data_supplier.dto.ResponseDTO;

public interface QuoteService {
  
  List<ResponseDTO> fetchData(List<String> usCodes);
  List<ApiProfileDTO> fetchProfileData(List<String> usCodes);
}
