package com.infina.wallet.client;

import com.infina.wallet.utility.GlobalRestClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component // Spring Boot'un bu sınıfı yönetmesi ve diğer sınıflara bağlayabilmesi için eklendi.
public class CurrencyClient {

    // En güncel ve modern HTTP istemcisi.
    private final RestClient restClient;

    // Eski hali:
    // this.restClient = RestClient.create("https://api.frankfurter.dev/v1");
    public CurrencyClient(GlobalRestClient globalRestClient) {
        this.restClient = globalRestClient.createClient("https://api.frankfurter.dev/v1");
    }

    // Dışarıdan para birimini (USD veya EUR) alıp, onun anlık TL (TRY) karşılığını döner.
    public Double getLiveRate(String currency) {
        try {
            // API'ye şu isteği attık: https://api.frankfurter.dev/v1/latest?base=TRY
            Map response = restClient.get()
                    .uri("/latest?base=TRY")
                    .retrieve()
                    .body(Map.class); // Gelen JSON verisini Java'daki Map (Anahtar-Değer) yapısına çevirir.

            // API'den dönen karmaşık JSON içinden "rates" haritasını koparıp alıyoruz.
            Map<String, Object> rates = (Map<String, Object>) response.get("rates");

            // İstediğimiz kurun (USD veya EUR) değerini alıp double tipinde yukarıya fırlatıyoruz.
            return (Double) rates.get(currency);
        } catch (Exception e) {
            // İnternet kesilirse veya API çökerse projenin durmaması için varsayılan güvenli bir kur döner.
            return 1.0;
        }
    }
}
