package aifriend.ai_backend.controller;

import aifriend.ai_backend.model.BillingRecord;
import aifriend.ai_backend.model.User;
import aifriend.ai_backend.repository.UserRepository;
import aifriend.ai_backend.service.BillingService;
import aifriend.ai_backend.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get current billing information for a user
     */
    @GetMapping("/current/{userId}")
    public ResponseEntity<?> getCurrentBilling(@PathVariable Long userId) {
        BillingRecord currentBilling = billingService.getCurrentBillingRecord(userId);
        BigDecimal totalBalance = billingService.getUserBalance(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("currentBilling", currentBilling);
        response.put("totalBalance", totalBalance);

        return ResponseEntity.ok(response);
    }

    /**
     * Get billing history for a user
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BillingRecord>> getBillingHistory(@PathVariable Long userId) {
        List<BillingRecord> history = billingService.getUserBillingHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Process payment for a specific billing record
     */
    @PostMapping("/pay/{recordId}")
    public ResponseEntity<?> processPayment(@PathVariable Long recordId) {
        try {
            BillingRecord record = billingService.getCurrentBillingRecord(recordId);
            if (record == null) {
                return ResponseEntity.notFound().build();
            }

            billingService.processPaymentForRecord(record);
            return ResponseEntity.ok(Map.of("success", true, "message", "Payment processed successfully"));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(Map.of("success", false, "message", "Payment failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error processing payment: " + e.getMessage()));
        }
    }

    /**
     * Add a payment method for a user
     */
    @PostMapping("/payment-method/{userId}")
    public ResponseEntity<?> addPaymentMethod(
            @PathVariable Long userId,
            @RequestParam String paymentMethodId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Ensure user has a Stripe customer ID
            String customerId = user.getStripeCustomerId();
            if (customerId == null) {
                customerId = stripeService.createCustomer(user);
                user.setStripeCustomerId(customerId);
                userRepository.save(user);
            }

            // Add the payment method
            String paymentMethod = stripeService.addPaymentMethod(customerId, paymentMethodId);
            return ResponseEntity.ok(Map.of("success", true, "paymentMethodId", paymentMethod));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "message", "Error adding payment method: " + e.getMessage()));
        }
    }

    /**
     * Get all payment methods for a user
     */
    @GetMapping("/payment-methods/{userId}")
    public ResponseEntity<?> getPaymentMethods(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            String customerId = user.getStripeCustomerId();
            if (customerId == null) {
                return ResponseEntity.ok(Map.of("paymentMethods", List.of()));
            }

            PaymentMethodCollection methods = stripeService.getPaymentMethods(customerId);
            return ResponseEntity.ok(Map.of("paymentMethods", methods.getData()));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error retrieving payment methods: " + e.getMessage()));
        }
    }
}
