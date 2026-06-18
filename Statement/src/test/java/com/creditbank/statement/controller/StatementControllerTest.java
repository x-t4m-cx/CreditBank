package com.creditbank.statement.controller;

import com.creditbank.statement.dto.request.LoanOfferDto;
import com.creditbank.statement.exception.StatementNotFoundException;
import com.creditbank.statement.handler.GlobalExceptionHandler;
import com.creditbank.statement.service.StatementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatementController.class)
@Import(GlobalExceptionHandler.class)
class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StatementService statementService;

    @Test
    void createStatement_returns200() throws Exception {
        when(statementService.createStatement(any()))
                .thenReturn(List.of(LoanOfferDto.builder()
                        .statementId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                        .term(24)
                        .build()));

        String body = """
                {
                  "amount": 250000.00,
                  "term": 24,
                  "firstName": "Ivan",
                  "lastName": "Petrov",
                  "middleName": "Sergeevich",
                  "email": "ivan.petrov@example.com",
                  "birthdate": "1995-03-12",
                  "passportSeries": "1234",
                  "passportNumber": "567890"
                }
                """;

        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statementId").value("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
    }

    @Test
    void createStatement_validationError_returns400_withErrorResponse() throws Exception {
        String body = """
                {
                  "amount": 10000.00,
                  "term": 24,
                  "firstName": "Ivan",
                  "lastName": "Petrov",
                  "email": "ivan.petrov@example.com",
                  "birthdate": "1995-03-12",
                  "passportSeries": "1234",
                  "passportNumber": "567890"
                }
                """;

        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.errors.amount").exists());
    }

    @Test
    void createStatement_malformedJson_returns400_withErrorResponse() throws Exception {
        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Deserialization failed"));
    }

    @Test
    void applyOffer_returns200() throws Exception {
        doNothing().when(statementService).applyOffer(any());

        LoanOfferDto dto = LoanOfferDto.builder()
                .statementId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                .requestedAmount(BigDecimal.valueOf(250000))
                .term(24)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void applyOffer_statementNotFound_returns404_withErrorResponse() throws Exception {
        doThrow(new StatementNotFoundException("Statement not found - id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                .when(statementService).applyOffer(any());

        LoanOfferDto dto = LoanOfferDto.builder()
                .statementId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                .requestedAmount(BigDecimal.valueOf(250000))
                .term(24)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Statement not found error"));
    }

    @Test
    void applyOffer_unhandledError_returns500_withErrorResponse() throws Exception {
        doThrow(new RuntimeException("boom"))
                .when(statementService).applyOffer(any());

        LoanOfferDto dto = LoanOfferDto.builder()
                .statementId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                .requestedAmount(BigDecimal.valueOf(250000))
                .term(24)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }
}

