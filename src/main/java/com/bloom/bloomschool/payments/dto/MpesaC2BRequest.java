package com.bloom.bloomschool.payments.dto;

import lombok.Data;

/**
 * Shape Safaricom posts to both the C2B Validation and Confirmation URLs.
 * Field names match the stable, publicly documented Daraja C2B spec.
 */
@Data
public class MpesaC2BRequest {
    private String TransactionType;
    private String TransID;
    private String TransTime;
    private String TransAmount;
    private String BusinessShortCode;
    /** The account number the payer typed in — expected to be the student admission number. */
    private String BillRefNumber;
    private String InvoiceNumber;
    private String OrgAccountBalance;
    private String ThirdPartyTransID;
    private String MSISDN;
    private String FirstName;
    private String MiddleName;
    private String LastName;
}
