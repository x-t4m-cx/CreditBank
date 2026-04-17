package com.creditbank.statement.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class StatementConfig {
    @Value("${deal.url}")
    private String dealUrl;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
                .baseUrl(dealUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public OpenAPI dealOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MC Statement")
                        .description("API for prescoring statements")
                        .version("v0.0.1")
                        .license(new License().name("Proprietary")));
    }
}
