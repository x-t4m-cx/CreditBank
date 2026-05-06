package com.creditbank.deal.service;

import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.entity.jsonb.Passport;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.repository.ClientRepository;
import com.creditbank.deal.repository.StatementRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(properties = {
        "CALCULATOR_URL=http://calculator:8080"
})
@AutoConfigureEmbeddedDatabase(
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplyOfferLockTest {

    @Autowired
    private DealService dealService;

    @MockitoBean
    private DocumentService documentService;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private UUID statementId;

    @BeforeEach
    void setup() {
        Mockito.doNothing().when(documentService)
                .sendFinishRegistration(any());

        statementRepository.deleteAllInBatch();
        clientRepository.deleteAllInBatch();

        Client client = Client.builder()
                .firstName("Test")
                .lastName("User")
                .birthDate(LocalDate.now().minusYears(20))
                .passport(new Passport())
                .email("test@example.com")
                .build();
        client = clientRepository.saveAndFlush(client);

        Statement statement = Statement.builder()
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(LocalDateTime.now())
                .statusHistory(new ArrayList<>())
                .build();

        statement = statementRepository.saveAndFlush(statement);
        statementId = statement.getStatementId();
    }

    @Test
    void shouldBlockSecondTransaction() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch t1Started = new CountDownLatch(1);

        Future<?> t1 = executor.submit(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                dealService.applyOffer(createOffer(statementId));
                t1Started.countDown();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });

        t1Started.await();

        long start = System.currentTimeMillis();

        Future<?> t2 = executor.submit(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                dealService.applyOffer(createOffer(statementId));
            });
        });

        t1.get();
        t2.get();

        long duration = System.currentTimeMillis() - start;

        assertTrue(duration >= 2500,
                "Second call not lock. duration: " + duration + " ms");

        executor.shutdown();
    }

    private LoanOfferDto createOffer(UUID id) {
        LoanOfferDto dto = new LoanOfferDto();
        dto.setStatementId(id);
        return dto;
    }
}