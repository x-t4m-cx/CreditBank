package com.creditbank.deal.controller;

import com.creditbank.deal.api.DocumentApi;
import com.creditbank.deal.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deal/document")
public class DocumentController implements DocumentApi {

    private final DocumentService service;

    @PostMapping("/{statementId}/send")
    public ResponseEntity<Void> sendDocuments(@PathVariable String statementId) {
        service.sendSendDocumentRequest(UUID.fromString(statementId));
        return ResponseEntity.status(202).build();
    }

    @PostMapping("/{statementId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable String statementId) {
        service.sendSignDocumentRequest(UUID.fromString(statementId));
        return ResponseEntity.status(202).build();
    }

    @PostMapping("/{statementId}/code")
    public ResponseEntity<Void> verifySesCode(@PathVariable String statementId) {
        service.sendCreditIssueCredit(UUID.fromString(statementId));
        return ResponseEntity.status(202).build();
    }
}
