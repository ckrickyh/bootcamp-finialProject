package com.finalproject.stock_data.entity;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.redis.core.RedisHash;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Read DB 
@Table(name = "ohlc_data")
@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class HistoryEntity implements Serializable{
  
  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "index")
  // private String index;
  private Integer index;
  private String code;
  private Double close;
  private Double open;
  private Integer volume;
  private Double high;
  private Double low;
  @Column(name = "symbol_link")
  private String symbolLink;
  @Column(name = "datetime")
  private LocalDate date;
}
