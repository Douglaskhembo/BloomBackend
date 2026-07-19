package com.bloom.bloomschool.auth.dto.Responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnboardStaffResponse {
    private UserResponse user;
    // No mail service exists yet, so the temp password is returned once, here, for
    // the admin to relay to the staff member — never stored or logged in plaintext.
    private String temporaryPassword;
}
