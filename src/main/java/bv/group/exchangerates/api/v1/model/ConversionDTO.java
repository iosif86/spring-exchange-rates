package bv.group.exchangerates.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ConversionDTO {
    private Boolean success;
    private Map<String, String> error;
    private Double result;
}