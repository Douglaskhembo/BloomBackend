package com.bloom.bloomschool.auth.dto.Responses;

import com.bloom.bloomschool.auth.model.User;
import lombok.Builder;
import lombok.Data;

/**
 * Internal result of {@code UserService.onboardParent} — deliberately not a REST response
 * DTO (carries the raw {@link User}, including its password hash) so callers must pick out
 * only the fields they need (typically just the uuid/userName) before it ever reaches a
 * controller response.
 */
@Data
@Builder
public class ParentLinkResult {
    /** False only when neither email nor phone was available to match/create against. */
    private boolean linked;
    private User user;
    private boolean newlyCreated;
    private String temporaryPassword;
}
