package aifriend.ai_backend.repository;

import aifriend.ai_backend.model.BillingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRecordRepository extends JpaRepository<BillingRecord, Long> {
    Optional<BillingRecord> findByUserIdAndBillingPeriod(Long userId, String billingPeriod);
    
    List<BillingRecord> findByUserId(Long userId);
    
    List<BillingRecord> findByUserIdAndIsPaidFalse(Long userId);
    
    List<BillingRecord> findByBillingPeriodAndIsPaidFalse(String billingPeriod);
    
    List<BillingRecord> findByUserIdOrderByBillingPeriodDesc(Long userId);
    
    List<BillingRecord> findByBillingPeriod(String billingPeriod);
    
    @Query("SELECT SUM(b.amountDue) FROM BillingRecord b WHERE b.userId = ?1 AND b.isPaid = false")
    Optional<BigDecimal> sumAmountDueByUserId(Long userId);
    
    @Query("SELECT SUM(b.amountDue) FROM BillingRecord b WHERE b.isPaid = true")
    Optional<BigDecimal> sumAmountDueByIsPaidTrue();
    
    @Query("SELECT SUM(b.amountDue) FROM BillingRecord b WHERE b.billingPeriod = ?1 AND b.isPaid = true")
    Optional<BigDecimal> sumAmountDueByBillingPeriodAndIsPaidTrue(String billingPeriod);
}
