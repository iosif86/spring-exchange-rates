package bv.group.exchangerates.services;

import bv.group.exchangerates.api.v1.model.RateDTO;
import bv.group.exchangerates.api.v1.model.enums.Currency;
import bv.group.exchangerates.exception.ExchangeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class ExchangeServiceImpl implements ExchangeService{
    private final RestClient restClient;
    private String apiKey;

    public ExchangeServiceImpl(RestClient restClient, @Value("${exchange.api.key}") String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    @Override
    public Map<String, Double> getExchangeRate(Currency from, Currency to) {
        log.info("ExchangeService : getExchangeRate is called");
        RateDTO rateDTO = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/live")
                        .queryParam("access_key", apiKey).queryParam("source", from)
                        .queryParam("currencies", to).queryParam("format", "1").build())
                .retrieve().body(RateDTO.class);
        if (!Objects.requireNonNull(rateDTO).getSuccess()){
            throw new ExchangeNotFoundException(rateDTO.getError().toString());
        }
        return rateDTO.getQuotes();
    }

    @Override
    public Map<String, Double> getExchangeRates(Currency from) {
        log.info("ExchangeService : getExchangeRates is called");
        RateDTO rateDTO = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/live")
                        .queryParam("access_key", apiKey).queryParam("source", from)
                        .queryParam("format", "1").build())
                .retrieve().body(RateDTO.class);
        if (!Objects.requireNonNull(rateDTO).getSuccess()){
            throw new ExchangeNotFoundException(rateDTO.getError().toString());
        }
        return rateDTO.getQuotes();
    }
}
