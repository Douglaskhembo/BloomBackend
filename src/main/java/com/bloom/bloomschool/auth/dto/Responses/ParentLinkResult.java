package com.bloom.bloomschool.auth.dto.Responses;

import com.bloom.bloomschool.auth.model.User;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ParentLinkResult {
    private boolean linked;
    private User user;
    private boolean newlyCreated;
    private String temporaryPassword;
}
