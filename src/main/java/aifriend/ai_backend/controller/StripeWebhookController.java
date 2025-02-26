package aifriend.ai_backend.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/api/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        if (sigHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing Stripe signature header");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Handle the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        }

        // Handle the event type
        switch (event.getType()) {
            case "payment_intent.succeeded":
                if (stripeObject instanceof PaymentIntent) {
                    PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                    // Handle successful payment
                    System.out.println("Payment succeeded: " + paymentIntent.getId());
                    // You could update your database here
                }
                break;
            case "payment_intent.payment_failed":
                if (stripeObject instanceof PaymentIntent) {
                    PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                    // Handle failed payment
                    System.out.println("Payment failed: " + paymentIntent.getId());
                    // You could update your database here
                }
                break;
            case "charge.succeeded":
                // Handle successful charge
                System.out.println("Charge succeeded: " + event.getId());
                break;
            case "charge.failed":
                // Handle failed charge
                System.out.println("Charge failed: " + event.getId());
                break;
            case "customer.subscription.created":
                // Handle subscription created
                System.out.println("Subscription created: " + event.getId());
                break;
            case "customer.subscription.updated":
                // Handle subscription updated
                System.out.println("Subscription updated: " + event.getId());
                break;
            case "customer.subscription.deleted":
                // Handle subscription deleted
                System.out.println("Subscription deleted: " + event.getId());
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok("Webhook received");
    }
}
