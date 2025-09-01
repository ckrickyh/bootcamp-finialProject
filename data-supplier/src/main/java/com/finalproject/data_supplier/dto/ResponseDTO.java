package com.finalproject.data_supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@NoArgsConstructor
public class ResponseDTO {
    // private Integer id;
    private String code;
    private Double currentPrice;
    private Double changePrice;
    private Double percentageChange;
    private Double high;
    private Double low;
    private Double open;
    private Double previousClose;
    private Long timestamp;
    // private LocalDateTime datetime;
    // private String logoLink;
}
