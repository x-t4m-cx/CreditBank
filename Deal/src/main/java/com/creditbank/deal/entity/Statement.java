package com.creditbank.deal.entity;

import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.jsonb.StatusHistory;
import com.creditbank.deal.model.LoanOffer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString(exclude = {"client", "credit"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statement")
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "statement_id")
    private UUID statementId;

    @OneToOne
    @JoinColumn(name = "client_id", unique = true, nullable = false)
    private Client client;

    @OneToOne
    @JoinColumn(name = "credit_id", unique = true)
    private Credit credit;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private LoanOffer appliedOffer;

    @Column(name = "sign_date")
    private LocalDateTime signDate;

    @Column(name = "ses_code")
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition = "jsonb", nullable = false)
    private List<StatusHistory> statusHistory;
}
