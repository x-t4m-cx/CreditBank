package com.creditbank.gateway.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @PostMapping("/{statementId}")
    public ResponseEntity<Void> createDocuments(@PathVariable String statementId) {

        return ResponseEntity.ok().build();
    }
}
