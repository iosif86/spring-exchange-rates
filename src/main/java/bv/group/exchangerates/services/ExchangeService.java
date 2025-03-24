package bv.group.exchangerates.services;

import bv.group.exchangerates.api.v1.model.enums.Currency;

import java.util.Map;

public interface ExchangeService {
    Map<String, Double> getExchangeRate(Currency from, Currency to);

    Map<String, Double> getExchangeRates(Currency from);
}
