package bv.group.exchangerates.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class RateDTO {
    private Boolean success;
    private Map<String, String> error;
    private String terms;
    private String privacy;
    private Long timestamp;
    private String source;
    private LinkedHashMap<String, Double> quotes;
}
