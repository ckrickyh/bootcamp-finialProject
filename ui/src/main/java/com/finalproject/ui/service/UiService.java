package com.finalproject.ui.service;

import java.util.List;
import com.finalproject.ui.dto.HistoryDTO;
import com.finalproject.ui.dto.ProfileDTO;
import com.finalproject.ui.dto.ResponseDTO;

public interface UiService {
  List<ResponseDTO> getAllFinnhubData();
  List<ProfileDTO> getUsProfile();
  List<HistoryDTO> getBackEndUsHistory(String usCode);
}
