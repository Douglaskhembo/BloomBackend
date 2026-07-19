package com.bloom.bloomschool.payments.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.payments.dto.MpesaC2BRequest;
import com.bloom.bloomschool.payments.dto.MpesaStkCallbackDto;
import com.bloom.bloomschool.payments.dto.StkPushRequest;
import com.bloom.bloomschool.payments.service.MpesaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments/mpesa")
@RequiredArgsConstructor
public class MpesaController {

    private static final Logger log = LoggerFactory.getLogger(MpesaController.class);

    private final MpesaService mpesaService;

    /** Authenticated: staff/parent-triggered "Pay Now" from the portal. */
    @PostMapping("/stk-push")
    public ResponseEntity<ApiResponse<?>> stkPush(@Valid @RequestBody StkPushRequest req) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.ok("STK push sent", mpesaService.initiateStkPush(req)));
    }

    /** Public — Safaricom calls this back after the payer completes/cancels the STK prompt. */
    @PostMapping("/stk-callback")
    public ResponseEntity<Map<String, Object>> stkCallback(@RequestBody MpesaStkCallbackDto payload) {
        try {
            mpesaService.handleStkCallback(payload);
        } catch (Exception e) {
            // Always ack 200 so Safaricom doesn't retry-storm us; the failure is logged for investigation.
            log.error("Failed processing STK callback: {}", payload, e);
        }
        return ResponseEntity.ok(Map.of("ResultCode", 0, "ResultDesc", "Accepted"));
    }

    /** Public — Safaricom asks before crediting the paybill; reject unknown admission numbers. */
    @PostMapping("/c2b/validation")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody MpesaC2BRequest req) {
        boolean known = mpesaService.isKnownAccount(req.getBillRefNumber());
        if (!known) {
            log.warn("Rejecting C2B payment for unknown account reference '{}'", req.getBillRefNumber());
            return ResponseEntity.ok(Map.of("ResultCode", "C2B00012", "ResultDesc", "Rejected - unknown account/admission number"));
        }
        return ResponseEntity.ok(Map.of("ResultCode", 0, "ResultDesc", "Accepted"));
    }

    /** Public — funds have already landed on the paybill; reconcile it. */
    @PostMapping("/c2b/confirmation")
    public ResponseEntity<Map<String, Object>> confirm(@RequestBody MpesaC2BRequest req) {
        try {
            mpesaService.handleC2BConfirmation(req);
        } catch (Exception e) {
            log.error("Failed processing C2B confirmation: {}", req, e);
        }
        return ResponseEntity.ok(Map.of("ResultCode", 0, "ResultDesc", "Accepted"));
    }
}
