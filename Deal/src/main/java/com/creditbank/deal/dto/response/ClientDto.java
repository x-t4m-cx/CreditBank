package com.creditbank.deal.dto.response;

import com.creditbank.deal.dto.request.EmploymentDto;
import com.creditbank.deal.entity.jsonb.Employment;
import com.creditbank.deal.entity.jsonb.Passport;
import com.creditbank.deal.enums.Gender;
import com.creditbank.deal.enums.MaritalStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private UUID clientId;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private Passport passport;
    private EmploymentDto employment;
    private String accountNumber;
}