package com.finalproject.stock_data.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.finalproject.stock_data.entity.ProfileEntity;
import com.finalproject.stock_data.service.ProviderService;

@Component
public class AppStartRunner implements CommandLineRunner {
  
  @Autowired
  ProviderService providerService;

  @Override
  public void run(String... args) throws Exception {
    
    //Delete all repository
    this.providerService.deleteAllOHLCs();
    this.providerService.deleteAllProfiles();
    System.out.println("================== db clean done ===================");

    // Important, must do
    // save profile to db (repository) at first
    List<ProfileEntity> profileEntities = this.providerService.getUsProfile();
    this.providerService.saveAllProfiles(profileEntities);

    System.out.println("================== read db profile done===============");
    System.out.println("========= ready ==========");


  }

}
