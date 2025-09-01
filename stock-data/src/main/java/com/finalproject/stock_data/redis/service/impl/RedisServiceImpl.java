package com.finalproject.stock_data.redis.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.stock_data.entity.HistoryEntity;
import com.finalproject.stock_data.lib.RedisManager;
import com.finalproject.stock_data.redis.FrontEndRedisHistoryDTO;
import com.finalproject.stock_data.redis.RedisHistoryDTO;
import com.finalproject.stock_data.redis.mapper.RedisDTOMapper;
import com.finalproject.stock_data.redis.service.RedisService;
import com.finalproject.stock_data.repository.HistoryRepository;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    RedisManager redisManager;
    @Autowired
    RedisDTOMapper redisDTOMapper;
    @Autowired
    HistoryRepository historyRepository;
    // @Autowired
    // ObjectMapper objectMapper;

    // ==== Redis Vincent ==== 
    //   Ꮎ redis is key-value Hashmap
    //   ① read 'cache - RedisDTO' first (loop by each Stock)
    //   ② if redis contains 'this' stock data in cache: 
    //          => write to 'FrontEndRedisDTO' <- map RedisDTO (source)
    //   ③ if redis not found this stock in cache,  
    //          => 3.1. write to 'FrontEndRedisDTO' <- map DBEntity (source)
    //          => 3.2. write to cache 'FrontEndRedisDTO' <- map DBEntity (source)

    // ============================

    // ====== Redis Ricky =====
    @Override
    public List<FrontEndRedisHistoryDTO> getCache(String targetCode)
        throws JsonProcessingException {
        
            // 1: check the targetCode existing in cache - redisDTOs[hashmap]
            // 2: if found, redisDTOs -> frontEndRedisDTOs
            // 3. else, DB action
            //   3.1: DB 'enities' findBy targetCode -> frontEndDTOs
            //   3.2: DB Each List<'enities'> findBy dbUniqueCode -> redisDTOs[hashmap]

            // !!!
            List<FrontEndRedisHistoryDTO> frontEndRedisHistoryDTOs = new ArrayList<>();

            String upTargetCode = targetCode.toUpperCase();
            String redisKey = "stock-" + upTargetCode; // Redis key
            // ① Check if targetCode exists in cache (redis)
            RedisHistoryDTO[] redisHistoryDTOs = this.redisManager.read(redisKey, RedisHistoryDTO[].class);
            if (redisHistoryDTOs != null) {
                System.out.println("Found in Redis: " + redisKey);
                // ② Map RedisDTOs to FrontEndDTOs
                for (RedisHistoryDTO redisHistoryDTO : redisHistoryDTOs) {
                    FrontEndRedisHistoryDTO frontEndRedisHistoryDTO = this.redisDTOMapper.map(redisHistoryDTO);
                    frontEndRedisHistoryDTOs.add(frontEndRedisHistoryDTO); // Add to the list
                }
            } else {
                //③ 3 DB fetching, find targetCode
                List<HistoryEntity> historyEntities = this.historyRepository.findByCode(upTargetCode);
                if (historyEntities.isEmpty()) {
                    throw new RuntimeException("StockCode not found: " + upTargetCode);
                } else {
                    // ③ 3.1 FrontEndRedisDTOs <- DB Entites
                    System.out.println("DB Found: " + upTargetCode);
                    for (HistoryEntity historyEntity : historyEntities) {
                        FrontEndRedisHistoryDTO frontEndRedisHistoryDTO = this.redisDTOMapper.map(historyEntity);
                        frontEndRedisHistoryDTOs.add(frontEndRedisHistoryDTO);
                    }
                }

                // ③ 3.2 each RedisManager < Key, redisDTOs > <- each DB Entity
                List<String> usCodes = this.historyRepository.findDistinctStockCodes();
                // List<String> usCodes = StockList.getList();
                for (String usCode : usCodes){
                    List<HistoryEntity> historyEntitiesByCode = this.historyRepository.findByCode(usCode);
                    List<RedisHistoryDTO> newRedisHistoryDTOs = historyEntitiesByCode.stream().map(e -> this.redisDTOMapper.mapDB(e))
                        .collect(Collectors.toList());
                    String renewRedisKey = "stock-" + usCode;
                    this.redisManager.write(renewRedisKey, newRedisHistoryDTOs, 30L); // Cache as an array
                }           
                //this.redisManager.write(renewRedisKey, new RedisHistoryDTO[]{redisHistoryDTO}, 30L); // Cache as an array
            }

        return frontEndRedisHistoryDTOs;
        }

}
