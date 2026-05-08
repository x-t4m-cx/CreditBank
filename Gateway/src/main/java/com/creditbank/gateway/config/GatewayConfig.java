package com.creditbank.gateway.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GatewayConfig {

    @Value("deal.url")
    private String dealUrl;
    @Value("statement.url")
    private String statementUrl;

    @Bean
    public RestClient dealRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(dealUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public RestClient statementRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(statementUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public OpenAPI dealOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MC Gateway")
                        .description("API for clients")
                        .version("v0.0.1")
                        .license(new License().name("Proprietary")));
    }
}
