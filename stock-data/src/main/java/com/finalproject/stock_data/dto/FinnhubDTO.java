package com.finalproject.stock_data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class FinnhubDTO{  //載入 data-provider 8090 既容器

    private Integer id;
    private String code;
    private Double currentPrice;
    private Double changePrice;
    private Double percentageChange;
    private Double high;
    private Double low;
    private Double open;
    private Double previousClose;
    private Long timestamp;

}
