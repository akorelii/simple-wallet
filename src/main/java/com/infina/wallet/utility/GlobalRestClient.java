package com.infina.wallet.utility;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component // Bu sınıfın Spring tarafından yönetilen ortak bir yardımcı araç olduğunu belirttik.
public class GlobalRestClient {

    /**
     * Uygulama içerisindeki herhangi bir istemcinin (örneğin CurrencyClient)
     * dış dünya ile konuşurken kullanacağı standart RestClient taslağını üretir.
     * @param baseUrl İstek atılacak dış API'nin ana adresi.
     * @return Yapılandırılmış temiz bir RestClient nesnesi.
     */
    public RestClient createClient(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json") // API'ye "Bana JSON formatında veri gönder" diyoruz.
                .defaultHeader("Content-Type", "application/json") // API'ye "Sana JSON formatında veri gönderiyorum" diyoruz.
                // İleride "Dış servislere güvenlik token'ı ekleyin" denirse,
                // o ayar sadece ve sadece bu satırın altına interceptor olarak eklenecek ve tüm sistem tek tıkla güncellenecektir.
                .build();
    }
}