package bv.group.exchangerates.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ConversionDTO {
    private Boolean success;
    private Map<String, String> error;
    private Double result;
}