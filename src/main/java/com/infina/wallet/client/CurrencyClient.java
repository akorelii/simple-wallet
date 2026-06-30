package com.infina.wallet.client;

import com.infina.wallet.utility.GlobalRestClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Component // Spring Boot'un bu sınıfı yönetmesi ve diğer sınıflara bağlayabilmesi için eklendi.
public class CurrencyClient {

    // En güncel ve modern HTTP istemcisi.
    private final RestClient restClient;

    // Eski hali:
    // this.restClient = RestClient.create("https://api.frankfurter.dev/v1");
    public CurrencyClient(GlobalRestClient globalRestClient) {
        this.restClient = globalRestClient.createClient("https://api.frankfurter.dev/v1");
    }

    public Double getLiveRate(String from, String to) {
        // Eğer iki cüzdanın para birimi aynıysa kur 1.0'dır
        if (from.equals(to)) {
            return 1.0;
        }

        try {
            // Dinamik olarak kaynak (from) ve hedef (to) birimlerini API'ye geçiyoruz !!!!!!!!!!!!!!!!!!!!
            Map response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/latest")
                            .queryParam("from", from)
                            .queryParam("to", to)
                            .build())
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> rates = (Map<String, Object>) response.get("rates");

            if (rates != null && rates.containsKey(to)) {
                return ((Number) rates.get(to)).doubleValue();
            }

            log.warn("Kur haritasında hedef birim bulunamadı: {}. Varsayılan 1.0 dönülüyor.", to);
            return 1.0;
        } catch (Exception e) {
            log.error("Kur API'sine bağlanırken hata oluştu: {}", e.getMessage());
            return 1.0;
        }
    }
}
