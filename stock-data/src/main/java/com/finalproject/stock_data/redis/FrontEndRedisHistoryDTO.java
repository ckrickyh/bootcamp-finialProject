package com.finalproject.stock_data.redis;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class FrontEndRedisHistoryDTO { // same as HistoryDTO -> it brings to HisotryDTO
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