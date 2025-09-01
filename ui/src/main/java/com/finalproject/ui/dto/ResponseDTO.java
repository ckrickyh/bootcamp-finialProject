package com.finalproject.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {
    private String code;
    private Double currentPrice;
    private Double changePrice;
    private Double percentageChange;
    private Double high;
    private Double low;
    private Double open;
    private Double previousClose;
    private Long timestamp;
    private String finnhubIndustry;
}
