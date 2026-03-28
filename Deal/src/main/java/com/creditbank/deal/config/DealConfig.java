package com.creditbank.deal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DealConfig {
    @Value("${calculator.base-url}")
    private String calculatorBaseUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(calculatorBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public OpenAPI calculatorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MC Deal")
                        .description("API for creating statement and client")
                        .version("v0.0.1")
                        .license(new License().name("Proprietary")));
    }
}
