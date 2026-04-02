package com.creditbank.deal.entity;

import com.creditbank.deal.enums.Gender;
import com.creditbank.deal.enums.MaritalStatus;
import com.creditbank.deal.entity.jsonb.Employment;
import com.creditbank.deal.entity.jsonb.Passport;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString(exclude = {"statement", "passport"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "passport", columnDefinition = "jsonb", nullable = false)
    private Passport passport;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "employment", columnDefinition = "jsonb")
    private Employment employment;

    @Column(name = "account_number")
    private String accountNumber;

    @OneToOne(mappedBy = "client")
    private Statement statement;
}
