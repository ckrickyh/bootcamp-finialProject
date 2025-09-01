package com.finalproject.stock_data.redis;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisHistoryDTO { // RedisHistoryDTO will be terminated each 30 seconds
  private Integer index;
  private String code;
  private Double close;
  private Double open;
  private Integer volume;
  private Double high;
  private Double low;
  private String symbolLink;
  private LocalDate date;
}
