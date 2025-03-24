package bv.group.exchangerates.controllers;

import bv.group.exchangerates.api.v1.model.enums.Currency;
import bv.group.exchangerates.services.ExchangeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/exchange")
@AllArgsConstructor
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
}
