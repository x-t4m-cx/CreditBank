package com.creditbank.deal.entity;

import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.jsonb.StatusHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "statement")
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "statement_id")
    private UUID statementId;


    @OneToOne
    @JoinColumn(name = "client_id", unique = true)
    private Client client;

    @OneToOne
    @JoinColumn(name = "credit_id", unique = true)
    private Credit credit;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status;


    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date")
    private LocalDateTime signDate;
    // Random UUID
    @Column(name = "ses_code")
    private UUID sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition = "jsonb")
    private StatusHistory statusHistory;
}
