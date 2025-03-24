package bv.group.exchangerates.services;

import bv.group.exchangerates.api.v1.model.ConversionDTO;
import bv.group.exchangerates.api.v1.model.RateDTO;
import bv.group.exchangerates.api.v1.model.enums.Currency;
import bv.group.exchangerates.exception.ExchangeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public Double convertToCurrency(Currency from, Currency to, Double amount) {
        log.info("ExchangeService : convertToCurrency is called");
        ConversionDTO conversionDTO = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/convert")
                        .queryParam("access_key", apiKey).queryParam("from", from)
                        .queryParam("to", to).queryParam("amount", amount).queryParam("format", "1").build())
                .retrieve().body(ConversionDTO.class);
        if (!Objects.requireNonNull(conversionDTO).getSuccess()){
            throw new ExchangeNotFoundException(conversionDTO.getError().toString());
        }
        return conversionDTO.getResult();
    }

    @Override
    public Map<String, Double> convertToCurrencies(Currency from, List<Currency> to, Double amount) {
        log.info("ExchangeService : convertToCurrencies is called");
        RateDTO rateDTO = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/live")
                        .queryParam("access_key", apiKey).queryParam("source", from)
                        .queryParam("currencies", to.stream().map(Enum::name).collect(Collectors.joining(",")))
                        .queryParam("format", "1").build())
                .retrieve().body(RateDTO.class);
        if (!Objects.requireNonNull(rateDTO).getSuccess()){
            throw new ExchangeNotFoundException(rateDTO.getError().toString());
        }
        Map<String, Double> quotes = rateDTO.getQuotes();
        return quotes.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue()*amount));
    }
}
