package com.finalproject.data_supplier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class ApiDTO {

    @JsonProperty("c") //web api 名, 咁先對到
    private Double currentPrice;

    @JsonProperty("d")
    private Double changePrice;

    @JsonProperty("dp")
    private Double percentageChange;

    @JsonProperty("h")
    private Double high;

    @JsonProperty("l")
    private Double low;

    @JsonProperty("o")
    private Double open;

    @JsonProperty("pc")
    private Double previousClose;

    @JsonProperty("t")
    private Long timestamp;


}
