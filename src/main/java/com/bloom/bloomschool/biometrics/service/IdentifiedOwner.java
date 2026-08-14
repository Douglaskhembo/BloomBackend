package com.bloom.bloomschool.biometrics.service;

import com.bloom.bloomschool.attendance.util.OwnerType;

import java.util.UUID;

/** Result of a successful 1:N fingerprint identification. */
public record IdentifiedOwner(UUID bioDataUuid, OwnerType ownerType, double score) {
}
