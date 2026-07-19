package com.bloom.bloomschool.payments.dto;

import lombok.Data;

import java.util.List;

/**
 * Shape Safaricom posts to the STK callback URL. Note the CallbackMetadata items
 * (Amount, MpesaReceiptNumber, TransactionDate, PhoneNumber) do NOT include the
 * merchant's AccountReference — it must be correlated server-side via CheckoutRequestID
 * against the transaction saved at STK-push time (see MpesaService).
 */
@Data
public class MpesaStkCallbackDto {
    private Body Body;

    @Data
    public static class Body {
        private StkCallback stkCallback;
    }

    @Data
    public static class StkCallback {
        private String MerchantRequestID;
        private String CheckoutRequestID;
        private int ResultCode;
        private String ResultDesc;
        private CallbackMetadata CallbackMetadata;
    }

    @Data
    public static class CallbackMetadata {
        private List<Item> Item;
    }

    @Data
    public static class Item {
        private String Name;
        private Object Value;
    }
}
