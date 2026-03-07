package com.creditbank.calculator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI calculatorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CreditBank Calculator API")
                        .description("API for generating loan offers and calculating credit schedule.")
                        .version("v0.0.1")
                        .license(new License().name("Proprietary")));
    }
}

