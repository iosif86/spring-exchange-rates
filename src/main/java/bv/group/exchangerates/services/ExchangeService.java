package bv.group.exchangerates.services;

import bv.group.exchangerates.api.v1.model.Conversion;
import bv.group.exchangerates.api.v1.model.Rate;
import bv.group.exchangerates.api.v1.model.enums.Currency;

import java.util.List;

public interface ExchangeService {
    Rate getExchangeRate(Currency from, Currency to);

    List<Rate> getExchangeRates(Currency from);

    Conversion convertToCurrency(Currency from, Currency to, Double amount);

    List<Conversion> convertToCurrencies(Currency from, List<Currency> to, Double amount);
}
