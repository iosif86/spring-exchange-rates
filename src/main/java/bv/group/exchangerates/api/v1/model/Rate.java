package bv.group.exchangerates.api.v1.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Rate {
    private String currencyPair;
    private Double value;
}
