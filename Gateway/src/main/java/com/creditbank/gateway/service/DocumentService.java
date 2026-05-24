package com.creditbank.gateway.service;

import com.creditbank.gateway.client.DealClient;
import com.creditbank.gateway.dto.request.VerifySesCodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DealClient dealClient;

    public void createDocuments(String statementId) {
        dealClient.createDocuments(statementId);
    }

    public void signDocuments(String statementId) {
        dealClient.signDocuments(statementId);
    }

    public void verifySesCode(String statementId, VerifySesCodeRequest code) {
        dealClient.verifySesCode(statementId, code);
    }
}
