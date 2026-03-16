package com.creditbank.calculator.controller;

import com.creditbank.calculator.dto.request.LoanStatementRequestDto;
import com.creditbank.calculator.dto.response.LoanOfferDto;
import com.creditbank.calculator.service.OfferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest {

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferController offerController;

    @Test
    void shouldReturnOffersAndStatusOkWhenRequestValid() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Petrov")
                .email("ivan.petrov@example.com")
                .birthdate(LocalDate.now().minusYears(30))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        List<LoanOfferDto> offers = List.of(
                LoanOfferDto.builder().rate(new BigDecimal("15")).build()
        );
        when(offerService.generateOffers(request)).thenReturn(offers);

        ResponseEntity<List<LoanOfferDto>> response = offerController.generateOffers(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(new BigDecimal("15"), response.getBody().get(0).getRate());
        verify(offerService).generateOffers(request);
    }

    @Test
    void shouldDelegateToServiceWithSameRequest() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("200000"))
                .term(24)
                .firstName("Anna")
                .lastName("Sidorova")
                .email("anna@example.com")
                .birthdate(LocalDate.now().minusYears(25))
                .passportSeries("5678")
                .passportNumber("123456")
                .build();

        when(offerService.generateOffers(request)).thenReturn(List.of());

        offerController.generateOffers(request);

        verify(offerService).generateOffers(request);
    }
}
