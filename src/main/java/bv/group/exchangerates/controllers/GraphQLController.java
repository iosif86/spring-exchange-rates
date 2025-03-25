package bv.group.exchangerates.controllers;

import bv.group.exchangerates.api.v1.model.Conversion;
import bv.group.exchangerates.api.v1.model.Rate;
import bv.group.exchangerates.api.v1.model.enums.Currency;
import bv.group.exchangerates.services.ExchangeService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class GraphQLController {
    private final ExchangeService exchangeService;

    @QueryMapping
    public Rate getRate(@Argument Currency from, @Argument Currency to){
        return exchangeService.getExchangeRate(from, to);
    }

    @QueryMapping
    public List<Rate> getRates(@Argument Currency from){
        return exchangeService.getExchangeRates(from);
    }

    @QueryMapping
    public Conversion getConversionToCurrency(@Argument Currency from, @Argument Currency to, @Argument Double amount){
        return exchangeService.convertToCurrency(from, to, amount);
    }

    @QueryMapping
    public List<Conversion> getConversionToCurrencies(@Argument Currency from, @Argument List<Currency> to, @Argument Double amount){
        return exchangeService.convertToCurrencies(from, to, amount);
    }
}
