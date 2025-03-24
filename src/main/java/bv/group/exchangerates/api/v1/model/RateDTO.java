package bv.group.exchangerates.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class RateDTO {
    private Boolean success;
    private Map<String, String> error;
    private String terms;
    private String privacy;
    private Long timestamp;
    private String source;
    private Map<String, Double> quotes;
}
