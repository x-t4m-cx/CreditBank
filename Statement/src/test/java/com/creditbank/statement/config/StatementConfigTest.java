package com.creditbank.statement.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "deal.url=http://localhost:8080")
class StatementConfigTest {

    @Autowired
    private RestClient restClient;

    @Test
    void restClientBean_isCreated() {
        assertThat(restClient).isNotNull();
    }
}

