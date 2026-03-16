package com.creditbank.calculator.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiConfigTest {

    @Test
    void shouldCreateOpenApiBeanWithExpectedInfo() {
        OpenApiConfig cfg = new OpenApiConfig();

        OpenAPI api = cfg.calculatorOpenApi();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("CreditBank Calculator API", api.getInfo().getTitle());
        assertEquals("v0.0.1", api.getInfo().getVersion());
    }
}

