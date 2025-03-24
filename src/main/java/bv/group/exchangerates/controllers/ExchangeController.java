package bv.group.exchangerates.controllers;

import bv.group.exchangerates.api.v1.model.enums.Currency;
import bv.group.exchangerates.services.ExchangeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exchange")
@AllArgsConstructor
@Validated
public class ExchangeController {
    private final ExchangeService exchangeService;

    @GetMapping("/rate")
    public ResponseEntity<Map<String, Double>> getRate(@RequestParam Currency from,
                                                       @RequestParam Currency to){
        return new ResponseEntity<>(exchangeService.getExchangeRate(from, to), HttpStatus.OK);
    }

    @GetMapping("/rates")
    public ResponseEntity<Map<String, Double>> getRates(@RequestParam Currency from){
        return new ResponseEntity<>(exchangeService.getExchangeRates(from),HttpStatus.OK) ;
    }

    @GetMapping("/conversion")
    public ResponseEntity<Double> getConversionToCurrency(@RequestParam Currency from,
                                                          @RequestParam Currency to, @RequestParam @Positive(message = "Amount must be greater than 0 !") Double amount){
        return new ResponseEntity<>(exchangeService.convertToCurrency(from, to, amount), HttpStatus.OK);
    }

    @GetMapping("/conversions")
    public ResponseEntity<Map<String, Double>> getConversionToCurrencies(@RequestParam Currency from,
                                                                         @RequestParam List<Currency> to, @RequestParam @Positive(message = "Amount must be greater than 0 !") Double amount){
        return new ResponseEntity<>(exchangeService.convertToCurrencies(from, to, amount), HttpStatus.OK);
    }
}
