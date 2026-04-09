package com.creditbank.deal.repository;

import com.creditbank.deal.entity.Statement;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StatementRepository extends JpaRepository<Statement, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Statement s WHERE s.statementId = :id")
    Optional<Statement> findByIdWithLock(@Param("id") UUID statementId);
}
