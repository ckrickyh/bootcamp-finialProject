package com.finalproject.stock_data.service;

import java.util.List;
import com.finalproject.stock_data.dto.FinnhubDTO;
import com.finalproject.stock_data.entity.FinnhubEntity;
import com.finalproject.stock_data.entity.HistoryEntity;
import com.finalproject.stock_data.entity.ProfileEntity;


public interface ProviderService {
  
  List<FinnhubEntity> getAllFinnhubData();
  ProfileEntity fetchProfileEntity(String profileStockCode);

  void saveFinnhubData(FinnhubDTO finnhubDTO);

  List<FinnhubEntity> saveAll(List<FinnhubEntity> ohlcEntities);
  void deleteAllOHLCs();

  List<HistoryEntity> getUsHistory(String usCode);

  List<ProfileEntity> getUsProfile();
  List<ProfileEntity> saveAllProfiles(List<ProfileEntity> profileEntities);
  void deleteAllProfiles();
}
