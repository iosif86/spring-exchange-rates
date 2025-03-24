package bv.group.exchangerates.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("exchange-rate-api")
                .packagesToScan("bv.group.exchangerates")
                .pathsToMatch("/api/v1/exchange/**")
                .addOpenApiCustomizer(apiCustomizer -> apiCustomizer.info(new Info().description("Exchange Rate API")
                        .title("Open API Exchange Rate").contact(new Contact().email("joseph_xltq@yahoo.com"))))
                .build();
    }
}
