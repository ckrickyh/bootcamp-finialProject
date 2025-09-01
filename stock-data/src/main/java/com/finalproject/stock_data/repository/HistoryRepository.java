package com.finalproject.stock_data.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.finalproject.stock_data.entity.HistoryEntity;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer> { // original Integer

  List<HistoryEntity> findByCode(String usCode);
  // List<String> findDistinctByCode(); return entity , not ideal

  @Query("SELECT DISTINCT h.code FROM HistoryEntity h")
  List<String> findDistinctStockCodes();
}
