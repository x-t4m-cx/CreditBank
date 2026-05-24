package com.creditbank.deal.dto.response;

import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Credit;
import com.creditbank.deal.entity.jsonb.StatusHistory;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.model.LoanOffer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementDto {

    private UUID statementId;

    private ClientDto client;

    private CreditDto credit;

    private ApplicationStatus status;

    private LocalDateTime creationDate;

    private LoanOffer appliedOffer;

    private LocalDateTime signDate;

    private String sesCode;

    private List<StatusHistory> statusHistory;
}
