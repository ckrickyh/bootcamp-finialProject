package com.finalproject.stock_data.entity;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "realtime")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FinnhubEntity implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "profile_code", referencedColumnName = "code", nullable = true)
    private ProfileEntity profileEntity; // Add this relationship

}
