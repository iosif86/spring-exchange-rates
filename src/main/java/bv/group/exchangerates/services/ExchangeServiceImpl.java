package bv.group.exchangerates.services;

import bv.group.exchangerates.api.v1.model.Conversion;
import bv.group.exchangerates.api.v1.model.ConversionDTO;
import bv.group.exchangerates.api.v1.model.Rate;
import bv.group.exchangerates.api.v1.model.RateDTO;
import bv.group.exchangerates.api.v1.model.enums.Currency;
import bv.group.exchangerates.exception.ExchangeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
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
    @Cacheable("rate")
    public Rate getExchangeRate(Currency from, Currency to) {
        log.info("ExchangeService : getExchangeRate is called");
        RateDTO rateDTO = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/live")
                        .queryParam("access_key", apiKey).queryParam("source", from)
                        .queryParam("currencies", to).queryParam("format", "1").build())
                .retrieve().body(RateDTO.class);
        if (!Objects.requireNonNull(rateDTO).getSuccess()){
            throw new ExchangeNotFoundException(rateDTO.getError().toString());
        }
        Map.Entry<String,Double> quote = rateDTO.getQuotes().entrySet().iterator().next();
        Rate rate = Rate.builder().currencyPair(quote.getKey()).value(quote.getValue()).build();
        return rate;
    }

    @Override
    @Cacheable("rates")
    public List<Rate> getExchangeRates(Currency from) {
        log.info("ExchangeService : getExchangeRates is called");
        RateDTO rateDTO = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/live")
                        .queryParam("access_key", apiKey).queryParam("source", from)
                        .queryParam("format", "1").build())
                .retrieve().body(RateDTO.class);
        if (!Objects.requireNonNull(rateDTO).getSuccess()){
            throw new ExchangeNotFoundException(rateDTO.getError().toString());
        }
        Rate rate;
        List<Rate> rates = new ArrayList<>();
        for (String currencyPair : rateDTO.getQuotes().keySet()) {
            rate = Rate.builder().currencyPair(currencyPair).value(rateDTO.getQuotes().get(currencyPair)).build();
            rates.add(rate);
        }
        return rates;
    }

    @Override
    @Cacheable("conversion")
    public Conversion convertToCurrency(Currency from, Currency to, Double amount) {
        log.info("ExchangeService : convertToCurrency is called");
        ConversionDTO conversionDTO = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/convert")
                        .queryParam("access_key", apiKey).queryParam("from", from)
                        .queryParam("to", to).queryParam("amount", amount).queryParam("format", "1").build())
                .retrieve().body(ConversionDTO.class);
        if (!Objects.requireNonNull(conversionDTO).getSuccess()){
            throw new ExchangeNotFoundException(conversionDTO.getError().toString());
        }
        Conversion conversion = Conversion.builder().from(from.name()).to(to.name()).amount(amount).result(conversionDTO.getResult()).build();
        return conversion;
    }

    @Override
    @Cacheable("conversions")
    public List<Conversion> convertToCurrencies(Currency from, List<Currency> to, Double amount) {
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
        Conversion conversion;
        List<Conversion> conversions = new ArrayList<>();
        int index = 0;
        for (String currencyPair : rateDTO.getQuotes().keySet()) {
            conversion = Conversion.builder().from(from.name()).to(to.get(index).name()).amount(amount)
                    .result(rateDTO.getQuotes().get(currencyPair)*amount).build();
            conversions.add(conversion);
            index++;
        }
        return conversions;
    }

    @CacheEvict(value = { "rate", "rates", "conversion", "conversions" }, allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.ttl}")
    public void clearCaches() {
        log.info("Caches are cleared");
    }

}
