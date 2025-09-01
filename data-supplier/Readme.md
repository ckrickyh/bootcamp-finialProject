import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class StockApiService {

    private static final String[] API_KEYS = {
        "API_KEY_1",
        "API_KEY_2",
        "API_KEY_3"
    };

    private static final String API_URL = "https://api.finnhub.io/api/v1/quote"; // Replace with your API URL

    @Autowired
    private RestTemplate restTemplate;

    public void callStocks(List<String> stockSymbols) {
        ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust the number of threads as needed

        for (int i = 0; i < stockSymbols.size(); i++) {
            final String stockSymbol = stockSymbols.get(i);
            final String apiKey = API_KEYS[i % API_KEYS.length];

            executor.submit(() -> callStockApi(stockSymbol, apiKey));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void callStockApi(String stockSymbol, String apiKey) {
        try {
            String url = API_URL + "?symbol=" + stockSymbol + "&token=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("API call successful for " + stockSymbol + " with key: " + apiKey + " Response: " + response);
        } catch (Exception e) {
            System.out.println("API call failed for " + stockSymbol + " with key: " + apiKey + " Error: " + e.getMessage());
        }
    }
}

https://open.alipay.com/portal/forum/post/194801035