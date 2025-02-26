package aifriend.ai_backend.service;

import aifriend.ai_backend.model.BillingRecord;
import aifriend.ai_backend.model.User;
import aifriend.ai_backend.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Create a new Stripe customer for a user
     */
    public String createCustomer(User user) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
            .setEmail(user.getEmail())
            .setName(user.getFirstName() + " " + user.getLastName())
            .setDescription("AI Friend user: " + user.getId())
            .setPhone(user.getPhoneNumber())
            .putMetadata("userId", user.getId().toString())
            .build();

        Customer customer = Customer.create(params);
        return customer.getId();
    }

    /**
     * Add a payment method to a customer
     */
    public String addPaymentMethod(String customerId, String paymentMethodId) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
            .setCustomer(customerId)
            .build();
        paymentMethod.attach(attachParams);

        // Set as default payment method
        CustomerUpdateParams params = CustomerUpdateParams.builder()
            .setInvoiceSettings(
                CustomerUpdateParams.InvoiceSettings.builder()
                    .setDefaultPaymentMethod(paymentMethodId)
                    .build()
            )
            .build();
        Customer.retrieve(customerId).update(params);

        return paymentMethod.getId();
    }

    /**
     * Get all payment methods for a customer
     */
    public PaymentMethodCollection getPaymentMethods(String customerId) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("type", "card");
        return PaymentMethod.list(params);
    }

    /**
     * Process a payment for a billing record
     */
    @Transactional
    public Charge processPayment(BillingRecord billingRecord, String customerId) throws StripeException {
        // Convert to cents for Stripe
        long amountInCents = billingRecord.getAmountDue()
            .multiply(new BigDecimal("100"))
            .longValue();

        ChargeCreateParams params = ChargeCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency("usd")
            .setCustomer(customerId)
            .setDescription("AI Friend messaging charges for period: " + billingRecord.getBillingPeriod())
            .putMetadata("billingPeriod", billingRecord.getBillingPeriod())
            .putMetadata("userId", billingRecord.getUserId().toString())
            .putMetadata("messageCount", String.valueOf(billingRecord.getMessageCount()))
            .build();

        return Charge.create(params);
    }

    /**
     * Process a payment directly with an amount
     */
    public Charge processPayment(String customerId, BigDecimal amount, String description) throws StripeException {
        // Convert to cents for Stripe
        long amountInCents = amount
            .multiply(new BigDecimal("100"))
            .longValue();

        ChargeCreateParams params = ChargeCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency("usd")
            .setCustomer(customerId)
            .setDescription(description)
            .build();

        return Charge.create(params);
    }

    /**
     * Update user with Stripe customer ID
     */
    @Transactional
    public User updateUserWithStripeCustomerId(Long userId, String stripeCustomerId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStripeCustomerId(stripeCustomerId);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with ID: " + userId);
    }
}
