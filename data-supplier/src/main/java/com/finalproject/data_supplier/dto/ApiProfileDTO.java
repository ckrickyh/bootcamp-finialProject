package com.finalproject.data_supplier.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class ApiProfileDTO {
    private String country;
    private String currency;
    private String estimateCurrency;
    private String exchange;
    private String finnhubIndustry;
    private String ipo; // Date in String format
    private String logo; // URL of the logo
    private Double marketCapitalization;
    private String name;
    private String phone;
    private Double shareOutstanding;
    private String ticker;
    private String weburl; // URL of the company website
}
