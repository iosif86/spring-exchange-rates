package bv.group.exchangerates.services;

import bv.group.exchangerates.api.v1.model.Conversion;
import bv.group.exchangerates.api.v1.model.ConversionDTO;
import bv.group.exchangerates.api.v1.model.Rate;
import bv.group.exchangerates.api.v1.model.RateDTO;
import bv.group.exchangerates.api.v1.model.enums.Currency;
import bv.group.exchangerates.config.RestClientConfig;
import bv.group.exchangerates.exception.ExchangeNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ExchangeServiceImpl.class)
@Import(RestClientConfig.class)
class ExchangeServiceImplTest {
    private static final String API_HOST = "api.exchangerate.host";
    private static final String ACCESS_KEY = "07422b6cf0b50f800e57793295b753ab";

    @Autowired
    ExchangeServiceImpl service;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void Should_ReturnRate_When_GetExchangeRate_And_SuccessTrue() throws JsonProcessingException {
        String uri = buildExchangeRateUri();
        RateDTO rateDTO = RateDTO.builder().success(true).quotes(Map.of("EURRON", 4.976977)).build();
        String response = objectMapper.writeValueAsString(rateDTO);
        // when
        this.server.expect(requestTo(uri)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        // then
        Rate rate = this.service.getExchangeRate(Currency.EUR, Currency.RON);
        assertThat(rate.getCurrencyPair()).isEqualTo("EURRON");
        assertThat(rate.getValue()).isEqualTo(4.976977);
    }

    @Test
    void Should_ReturnError_When_GetExchangeRate_And_SuccessFalse() throws JsonProcessingException {
        String uri = buildExchangeRateUri();
        RateDTO rateDTO = RateDTO.builder().success(false).error(Map.of("code", "201",
                "type", "invalid_source_currency", "info", "You have supplied an invalid Source Currency.")).build();
        String response = objectMapper.writeValueAsString(rateDTO);
        // when
        this.server.expect(requestTo(uri)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        // then
        assertThrows(ExchangeNotFoundException.class,
                ()-> this.service.getExchangeRate(Currency.EUR, Currency.RON));
    }

    @Test
    void Should_ReturnMultipleRates_When_GetExchangeRates_And_SuccessTrue() throws JsonProcessingException {
        String uri = buildExchangeRatesUri();
        RateDTO rateDTO = RateDTO.builder().success(true).quotes(Map.of("EURAED", 3.996877, "EURAFN", 77.13611 )).build();
        String response = objectMapper.writeValueAsString(rateDTO);
        // when
        this.server.expect(requestTo(uri)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        // then
        List<Rate> rates = this.service.getExchangeRates(Currency.EUR);
        assertThat(rates).size().isGreaterThan(1);
    }

    @Test
    void Should_ReturnError_When_GetExchangeRates_And_SuccessFalse() throws JsonProcessingException {
        String uri = buildExchangeRatesUri();
        RateDTO rateDTO = RateDTO.builder().success(false).error(Map.of("code", "201",
                "type", "invalid_source_currency", "info", "You have supplied an invalid Source Currency.")).build();
        String response = objectMapper.writeValueAsString(rateDTO);
        // when
        this.server.expect(requestTo(uri)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        // then
        assertThrows(ExchangeNotFoundException.class,
                ()-> this.service.getExchangeRates(Currency.EUR));
    }

    @Test
    void Should_ReturnValueConversion_When_ConvertToCurrency_And_SuccessTrue() throws JsonProcessingException {
        String uri = buildConvertToCurrencyUri();
        ConversionDTO conversionDTO = ConversionDTO.builder().success(true).result(49.57856).build();
        String response = objectMapper.writeValueAsString(conversionDTO);
        // when
        this.server.expect(requestTo(uri)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        // then
        Conversion conversion = this.service.convertToCurrency(Currency.EUR, Currency.RON, Double.valueOf(10));
        assertThat(conversion.getResult()).isEqualTo(49.57856);
    }

    @Test
    void Should_ReturnError_When_ConvertToCurrency_And_SuccessFalse() throws JsonProcessingException {
        String uri = buildConvertToCurrencyUri();
        ConversionDTO conversionDTO = ConversionDTO.builder().success(false).error(Map.of("code", "201",
                "type", "invalid_source_currency", "info", "You have supplied an invalid Source Currency.")).build();
        String response = objectMapper.writeValueAsString(conversionDTO);
        // when
        this.server.expect(requestTo(uri)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        // then
        assertThrows(ExchangeNotFoundException.class,
                ()-> this.service.convertToCurrency(Currency.EUR, Currency.RON, 10.0));
    }

    @Test
    void Should_ReturnValueConversions_When_ConvertToCurrencies_And_SuccessTrue() throws JsonProcessingException {
        String uri = buildConvertToCurrenciesUri();
        RateDTO rateDTO = RateDTO.builder().success(true).quotes(Map.of("EURRON", 4.976977, "EURAED", 3.996877 )).build();
        String response = objectMapper.writeValueAsString(rateDTO);
        // when
        this.server.expect(requestTo(uri)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        // then
        List<Conversion> conversions = this.service.convertToCurrencies(Currency.EUR, List.of(Currency.RON, Currency.AED), 10.0);
        assertThat(conversions.get(0).getResult()).isEqualTo(49.769769999999994);
        assertThat(conversions.get(1).getResult()).isEqualTo(39.96877);
    }

    @Test
    void Should_ReturnError_When_ConvertToCurrencies_And_SuccessFalse() throws JsonProcessingException {
        String uri = buildConvertToCurrenciesUri();
        RateDTO rateDTO = RateDTO.builder().success(false).error(Map.of("code", "201",
                "type", "invalid_source_currency", "info", "You have supplied an invalid Source Currency.")).build();
        String response = objectMapper.writeValueAsString(rateDTO);
        // when
        this.server.expect(requestTo(uri)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        // then
        assertThrows(ExchangeNotFoundException.class,
                ()-> this.service.convertToCurrencies(Currency.EUR, List.of(Currency.RON, Currency.AED), 10.0));
    }

    private String buildExchangeRateUri(){
        String uri = UriComponentsBuilder.newInstance().scheme("http").host(API_HOST).path("/live").queryParam("access_key", ACCESS_KEY)
                .queryParam("source", Currency.EUR).queryParam("currencies", Currency.RON)
                .queryParam("format", "1").build().toUriString();
        return uri;
    }

    private String buildExchangeRatesUri(){
        String uri = UriComponentsBuilder.newInstance().scheme("http").host(API_HOST).path("/live").queryParam("access_key", ACCESS_KEY)
                .queryParam("source", Currency.EUR)
                .queryParam("format", "1").build().toUriString();
        return uri;
    }

    private String buildConvertToCurrencyUri(){
        String uri = UriComponentsBuilder.newInstance().scheme("http").host(API_HOST).path("/convert").queryParam("access_key", ACCESS_KEY)
                .queryParam("from", Currency.EUR).queryParam("to", Currency.RON)
                .queryParam("amount", 10.0)
                .queryParam("format", "1").build().toUriString();
        return uri;
    }

    private String buildConvertToCurrenciesUri() {
        String uri = UriComponentsBuilder.newInstance().scheme("http").host(API_HOST).path("/live").queryParam("access_key", ACCESS_KEY)
                .queryParam("source", Currency.EUR).queryParam("currencies", "RON,AED")
                .queryParam("format", "1").build().toUriString();
        return uri;
    }
}