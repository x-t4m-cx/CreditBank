package com.creditbank.gateway.controller;


import com.creditbank.gateway.api.DocumentApi;
import com.creditbank.gateway.dto.request.VerifySesCodeRequest;
import com.creditbank.gateway.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;
    @PostMapping("/{statementId}")
    public ResponseEntity<Void> createDocuments(@PathVariable String statementId) {
        documentService.createDocuments(statementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{statementId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable String statementId) {
        documentService.signDocuments(statementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{statementId}/code")
    public ResponseEntity<Void> verifySesCode(@PathVariable String statementId,
                                              @RequestBody VerifySesCodeRequest code) {
        documentService.verifySesCode(statementId, code);
        return ResponseEntity.ok().build();
    }
}
