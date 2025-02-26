package aifriend.ai_backend.service;

import aifriend.ai_backend.model.*;
import aifriend.ai_backend.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
public class BillingService {
    private static final BigDecimal RATE_PER_MESSAGE = new BigDecimal("0.018");
    
    @Autowired
    private BillingRecordRepository billingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StripeService stripeService;
    
    @Transactional
    public void recordMessageSent(Long userId) {
        // Get current YYYY-MM
        String currentPeriod = YearMonth.now().toString();
        
        BillingRecord record = billingRepository.findByUserIdAndBillingPeriod(userId, currentPeriod)
            .orElseGet(() -> {
                BillingRecord newRecord = new BillingRecord();
                newRecord.setUserId(userId);
                newRecord.setBillingPeriod(currentPeriod);
                newRecord.setRatePerMessage(RATE_PER_MESSAGE);
                return newRecord;
            });
        
        record.setMessageCount(record.getMessageCount() + 1);
        record.calculateAmountDue(); // This will use the stored rate per message
        
        billingRepository.save(record);
    }
    
    public BigDecimal getUserBalance(Long userId) {
        return billingRepository.sumAmountDueByUserId(userId)
            .orElse(BigDecimal.ZERO);
    }
    
    public BillingRecord getCurrentBillingRecord(Long userId) {
        String currentPeriod = YearMonth.now().toString();
        return billingRepository.findByUserIdAndBillingPeriod(userId, currentPeriod)
            .orElse(null);
    }
    
    /**
     * Process monthly billing for all users
     * Runs at midnight on the 1st day of each month
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void processMonthlyBilling() {
        // Get previous month in YYYY-MM format
        String previousMonth = YearMonth.now().minusMonths(1).toString();
        
        // Find all unpaid billing records for the previous month
        List<BillingRecord> unpaidRecords = billingRepository.findByBillingPeriodAndIsPaidFalse(previousMonth);
        
        for (BillingRecord record : unpaidRecords) {
            try {
                processPaymentForRecord(record);
            } catch (Exception e) {
                // Log the error but continue processing other records
                System.err.println("Error processing payment for user " + record.getUserId() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Process payment for a specific billing record
     */
    @Transactional
    public void processPaymentForRecord(BillingRecord record) throws StripeException {
        // Skip if already paid or amount is zero
        if (record.getIsPaid() || record.getAmountDue().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        // Get the user
        User user = userRepository.findById(record.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + record.getUserId()));
        
        // Ensure user has a Stripe customer ID
        String customerId = user.getStripeCustomerId();
        if (customerId == null) {
            // Create a new customer in Stripe
            customerId = stripeService.createCustomer(user);
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }
        
        // Process the payment
        Charge charge = stripeService.processPayment(record, customerId);
        
        // Mark as paid if successful
        if (charge != null && charge.getPaid()) {
            record.setIsPaid(true);
            billingRepository.save(record);
        }
    }
    
    /**
     * Get billing history for a user
     */
    public List<BillingRecord> getUserBillingHistory(Long userId) {
        return billingRepository.findByUserIdOrderByBillingPeriodDesc(userId);
    }
}
