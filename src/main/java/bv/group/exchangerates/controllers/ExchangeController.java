package bv.group.exchangerates.controllers;

import bv.group.exchangerates.api.v1.model.Conversion;
import bv.group.exchangerates.api.v1.model.Rate;
import bv.group.exchangerates.api.v1.model.enums.Currency;
import bv.group.exchangerates.services.ExchangeService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange")
@AllArgsConstructor
@Validated
public class ExchangeController {
    private final ExchangeService exchangeService;

    @Operation(summary = "Get exchange rate from Currency A to Currency B")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the exchange rate",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Rate.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid currency: <currency_name>",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "An error occurred!",
                    content = @Content) })
    @GetMapping("/rate")
    @RateLimiter(name = "rateLimit")
    public ResponseEntity<Rate> getRate(@RequestParam Currency from,
                                        @RequestParam Currency to){
        return new ResponseEntity<>(exchangeService.getExchangeRate(from, to), HttpStatus.OK);
    }

    @Operation(summary = "Get all exchange rates from Currency A")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the exchange rates",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Rate.class))) }),
            @ApiResponse(responseCode = "400", description = "Invalid currency: <currency_name>",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "An error occurred!",
                    content = @Content) })
    @GetMapping("/rates")
    @RateLimiter(name = "rateLimit")
    public ResponseEntity<List<Rate>> getRates(@RequestParam Currency from){
        return new ResponseEntity<>(exchangeService.getExchangeRates(from),HttpStatus.OK) ;
    }

    @Operation(summary = "Get value conversion from Currency A to Currency B")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful conversion!",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Conversion.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid currency: <currency_name>",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Amount must be greater than 0 !",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "An error occurred!",
                    content = @Content) })
    @GetMapping("/conversion")
    @RateLimiter(name = "rateLimit")
    public ResponseEntity<Conversion> getConversionToCurrency(@RequestParam Currency from,
                                                              @RequestParam Currency to, @RequestParam @Positive(message = "Amount must be greater than 0 !") Double amount){
        return new ResponseEntity<>(exchangeService.convertToCurrency(from, to, amount), HttpStatus.OK);
    }

    @Operation(summary = "Get value conversion from Currency A to a list of supplied currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful conversions!",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Conversion.class))) }),
            @ApiResponse(responseCode = "400", description = "Invalid currency: <currency_name>",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Amount must be greater than 0 !",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "An error occurred!",
                    content = @Content) })
    @GetMapping("/conversions")
    @RateLimiter(name = "rateLimit")
    public ResponseEntity<List<Conversion>> getConversionToCurrencies(@RequestParam Currency from,
                                                                         @RequestParam List<Currency> to, @RequestParam @Positive(message = "Amount must be greater than 0 !") Double amount){
        return new ResponseEntity<>(exchangeService.convertToCurrencies(from, to, amount), HttpStatus.OK);
    }
}
