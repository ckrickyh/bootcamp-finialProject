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
    model.addAttribute("baseUrl", resolveBaseUrl());
    return "heatmap";
  }

  @Override
  public String loadStockHistory(Model model, String usCode){
    List<HistoryDTO> stockHistory = this.uiService.getBackEndUsHistory(usCode);
    model.addAttribute("stockData", stockHistory);
    return "candlestick"; 
  }

  private String resolveBaseUrl() {
    if (baseUrl != null && !baseUrl.trim().isEmpty()) {
      return baseUrl.trim();
    }

    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return "http://localhost:8102";
    }

    HttpServletRequest request = attributes.getRequest();
    String scheme = extractFirstHeader(request, "X-Forwarded-Proto", request.getScheme());
    String forwardedHost = extractFirstHeader(request, "X-Forwarded-Host", null);
    String forwardedPortHeader = extractFirstHeader(request, "X-Forwarded-Port", null);

    if (forwardedHost != null && forwardedHost.contains(":")) {
      return scheme + "://" + forwardedHost;
    }

    String host = forwardedHost != null && !forwardedHost.isEmpty()
        ? forwardedHost
        : request.getServerName();

    Integer port = null;
    if (forwardedPortHeader != null && !forwardedPortHeader.isEmpty()) {
      try {
        port = Integer.parseInt(forwardedPortHeader);
      } catch (NumberFormatException ignored) {
        port = null;
      }
    }
    if (port == null) {
      port = request.getServerPort();
    }

    boolean isDefaultPort =
        ("http".equalsIgnoreCase(scheme) && port == 80)
            || ("https".equalsIgnoreCase(scheme) && port == 443);

    StringBuilder base = new StringBuilder().append(scheme).append("://").append(host);
    if (!isDefaultPort) {
      base.append(":").append(port);
    }
    return base.toString();
  }

  private String extractFirstHeader(HttpServletRequest request, String headerName, String fallback) {
    String headerValue = request.getHeader(headerName);
    if (headerValue == null || headerValue.isEmpty()) {
      return fallback;
    }
    int commaIndex = headerValue.indexOf(',');
    return commaIndex >= 0 ? headerValue.substring(0, commaIndex).trim() : headerValue.trim();
  }
}
