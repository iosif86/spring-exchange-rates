package bv.group.exchangerates.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class RestClientConfig {
    @Value("${exchange.api.url}")
    private String apihost;

    @Bean
    public RestClient getRestClient() {
        return RestClient.builder().baseUrl(apihost).build();
    }
}