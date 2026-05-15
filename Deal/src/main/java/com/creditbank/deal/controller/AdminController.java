package com.creditbank.deal.controller;

import com.creditbank.deal.api.AdminApi;
import com.creditbank.deal.dto.response.StatementDto;
import com.creditbank.deal.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deal/admin/")
public class AdminController implements AdminApi {

    private final StatementService statementService;

    @GetMapping("/statement/{statementId}")
    public ResponseEntity<StatementDto> getStatementById(@PathVariable String statementId){
        return ResponseEntity.ok(statementService.getStatementById(statementId));
    }

    @GetMapping("/statement")
    public ResponseEntity<Page<StatementDto>> getAllStatements(
            @PageableDefault(
                    size = 20,
                    sort = "creationDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable){
        return ResponseEntity.ok(statementService.getAllStatements(pageable));
    }
}
