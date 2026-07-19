package com.bloom.bloomschool.payments.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.payments.service.BankPaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public inbound webhook endpoints for bank payment notifications. See
 * {@link BankPaymentService} for the caveat on payload shape / signature scheme accuracy —
 * confirm each bank's real spec before enabling it (banks.<bank>.enabled=true) in production.
 */
@RestController
@RequestMapping("/payments/banks")
@RequiredArgsConstructor
public class BankWebhookController {

    private static final Logger log = LoggerFactory.getLogger(BankWebhookController.class);

    private final BankPaymentService bankPaymentService;

    @PostMapping("/equity/callback")
    public ResponseEntity<ApiResponse<?>> equityCallback(@RequestBody String rawBody,
                                                           @RequestHeader(value = "Signature", required = false) String signature) {
        return ack(() -> bankPaymentService.handleEquityCallback(rawBody, signature));
    }

    @PostMapping("/kcb/callback")
    public ResponseEntity<ApiResponse<?>> kcbCallback(@RequestBody String rawBody,
                                                        @RequestHeader(value = "Signature", required = false) String signature) {
        return ack(() -> bankPaymentService.handleKcbCallback(rawBody, signature));
    }

    @PostMapping("/coop/callback")
    public ResponseEntity<ApiResponse<?>> coopCallback(@RequestBody String rawBody,
                                                         @RequestHeader(value = "Signature", required = false) String signature) {
        return ack(() -> bankPaymentService.handleCoopCallback(rawBody, signature));
    }

    private ResponseEntity<ApiResponse<?>> ack(Runnable action) {
        try {
            action.run();
        } catch (SecurityException e) {
            log.warn("Rejected bank webhook: {}", e.getMessage());
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed processing bank webhook", e);
            // Ack 200 anyway so the bank doesn't retry-storm us; the raw payload was already logged for investigation.
        }
        return ResponseEntity.ok(ApiResponse.ok("Accepted"));
    }
}
