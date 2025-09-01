package com.finalproject.data_supplier.stockList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StockList {

    // public static final List<String> getListByCsv(String filePath) {
    //   // /Users/hochakkong/Documents/github/finalproject/stock-data/src/main/java/com/finalproject/stock_data/stockList/StockList.csv
    //   List<String> lines = new ArrayList<>();
    //   try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
    //       String line;
    //       while ((line = br.readLine()) != null) {
    //           lines.add(line);
    //       }
    //   } catch (IOException e) {
    //       e.printStackTrace();
    //   }
    //   return lines; //List<String>
    // }

  public static final List<String> usCodes = new ArrayList<>(Arrays.asList(
        "TSLA", "NVDA", "AAPL", "META", "GOOGL",
        "MSFT", "INTC", "AVGO", "SOUN", "AMD",
        "NFLX", "UBER", "TGT", "BBAI", "OSS"
        ,"DNUT", "COMP", "OPEN", "MARA", "IOVA"
        ,"WMT",  "HD", "AMZN", "NIO", "COST"
        ,"PYPL", "ADBE", "CRM", "ORCL", "IBM"
        // ,"CAH", "ACGL", "BLL", "CEGVV", "CLX"
        // ,"CL", "LMT", "CPRT", "CAH", "MDLZ"
        // ,"FANG",  "EL", "EXC", "LOW", "MAR"
        // ,"GILD", "HAL", "LMT", "MCD", "NKE"
        // ,"LHX", "MET", "MO", "MS", "V"
        // ,"LLY", "ORCL", "JNJ", "PG", "KO"
  ));

  public static List<String> getList() {
      return usCodes;
  }
}
