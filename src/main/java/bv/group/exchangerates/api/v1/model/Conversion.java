package bv.group.exchangerates.api.v1.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Conversion {
    private String from;
    private String to;
    private Double amount;
    private Double result;
}
