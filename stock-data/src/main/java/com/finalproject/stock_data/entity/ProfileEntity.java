package com.finalproject.stock_data.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "profile")
@Entity
@ToString
public class ProfileEntity implements Serializable{
    @Id
    private String code;
    
    private String country;
    private String currency;
    private String estimateCurrency;
    private String exchange;
    @Column(name = "finnhubIndustry")
    private String finnhubIndustry;
    private String ipo; // Date in String format
    private String logo; // URL of the logo
    private Double marketCapitalization;
    private String name;
    private String phone;
    private Double shareOutstanding;
    private String weburl; // URL of the company website
    
}
