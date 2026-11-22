package com.finalproject.stock_data.redis.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalproject.stock_data.entity.HistoryEntity;
import com.finalproject.stock_data.lib.RedisManager;
import com.finalproject.stock_data.redis.RedisHistoryDTO;
import com.finalproject.stock_data.redis.mapper.RedisDTOMapper;
import com.finalproject.stock_data.repository.HistoryRepository;

@ExtendWith(MockitoExtension.class)
class RedisServiceImplTest {
  @Mock
  private RedisManager redisManager;
  @Mock
  private HistoryRepository historyRepository;
  @Spy
  private RedisDTOMapper redisDTOMapper = new RedisDTOMapper();
  @InjectMocks
  private RedisServiceImpl redisService;
  @Captor
  private ArgumentCaptor<List<RedisHistoryDTO>> cachedHistory;

  @Test
  void cachesOnlyRequestedStockOnCacheMiss() throws JsonProcessingException {
    List<HistoryEntity> historyEntities = List.of(
        HistoryEntity.builder().index(1).code("MARA").build(),
        HistoryEntity.builder().index(2).code("MARA").build());
    when(historyRepository.findByCode("MARA")).thenReturn(historyEntities);

    var result = redisService.getCache("mara");

    assertEquals(2, result.size());
    verify(historyRepository).findByCode("MARA");
    verify(historyRepository, never()).findDistinctStockCodes();

    verify(redisManager).write(eq("stock-MARA"), cachedHistory.capture(), eq(30L));
    assertEquals(2, cachedHistory.getValue().size());
  }
}

/*
 * 心智圖：cache miss（Redis 沒有快取）
 * ├─ 準備 Arrange
 * │  ├─ HistoryRepository 回傳 2 筆 MARA 資料
 * │  └─ RedisManager 是 mock，未設定 read 回應時會回傳 null
 * ├─ 執行 Act
 * │  └─ getCache("mara")：轉成大寫 MARA，再查詢資料庫
 * └─ 驗證 Assert
 *    ├─ 回傳結果有 2 筆
 *    ├─ 有查 MARA，沒有查所有股票代碼
 *    └─ 寫入 stock-MARA，TTL TimeToLife 為 30 秒，快取 2 筆資料
 */
