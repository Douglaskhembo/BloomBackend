package com.bloom.bloomschool.staff.dto;

import com.bloom.bloomschool.staff.entity.StaffPaymentDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class StaffPaymentDetailsRequest {
    @NotBlank private String staffId;
    @NotNull private StaffPaymentDetails.PaymentMethod paymentMethod;

    private UUID bankUuid;
    private String bankAccountNumber;
    private String bankAccountName;
    private String bankBranch;

    private UUID mobileMoneyProviderUuid;
    private String mobileNumber;
    private String mobileAccountName;

    private UUID paymentTypeUuid;
}
