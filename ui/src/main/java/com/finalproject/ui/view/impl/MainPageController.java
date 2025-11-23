package com.finalproject.ui.view.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.finalproject.ui.dto.HistoryDTO;
import com.finalproject.ui.dto.ResponseDTO;
import com.finalproject.ui.service.UiService;
import com.finalproject.ui.view.MainPageOperation;
import jakarta.servlet.http.HttpServletRequest;

@Controller // ! return html
public class MainPageController implements MainPageOperation {
  @Autowired
  UiService uiService;

  @Value("${BASE_URL:}") // Inject BASE_URL from env/properties, empty default to use request-based URL
  private String baseUrl;

  @Override
  public String loadStockTable(Model model) {
    List<ResponseDTO> stockData = this.uiService.getAllFinnhubData();
    model.addAttribute("stocks", stockData);
    
    // Construct baseUrl: use BASE_URL env var if set, otherwise derive from request
    // This works for both local (localhost:8102) and GCP VM (34.169.230.10:8102)
    String effectiveBaseUrl = baseUrl;
    if (effectiveBaseUrl == null || effectiveBaseUrl.trim().isEmpty()) {
      // Get request from RequestContextHolder to avoid changing interface signature
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        // Include port if it's not the default port for the scheme
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
          effectiveBaseUrl = scheme + "://" + serverName + ":" + serverPort;
        } else {
          effectiveBaseUrl = scheme + "://" + serverName;
        }
      } else {
        // Fallback if request is not available (shouldn't happen in normal web context)
        effectiveBaseUrl = "http://localhost:8102";
      }
    }
    
    model.addAttribute("baseUrl", effectiveBaseUrl);
    return "heatmap";
  }

  @Override
  public String loadStockHistory(Model model, String usCode){
    List<HistoryDTO> stockHistory = this.uiService.getBackEndUsHistory(usCode);
    model.addAttribute("stockData", stockHistory);
    return "candlestick"; 
  }
}
