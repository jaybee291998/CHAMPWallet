package com.cwallet.champwallet.repository.account;

import com.cwallet.champwallet.models.account.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
   @Query(
           value = "SELECT * FROM verification WHERE timestamp BETWEEN :startDate AND :endDate AND account_id=:accountID ORDER BY timestamp DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY",
           nativeQuery = true
   )
    Verification findLatestTimestampByAccountID(LocalDateTime startDate, LocalDateTime endDate, long accountID);
}
