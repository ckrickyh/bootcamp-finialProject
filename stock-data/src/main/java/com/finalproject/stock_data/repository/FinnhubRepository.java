package com.finalproject.stock_data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.finalproject.stock_data.entity.FinnhubEntity;

@Repository
public interface FinnhubRepository extends JpaRepository<FinnhubEntity, Long>{
  
}
