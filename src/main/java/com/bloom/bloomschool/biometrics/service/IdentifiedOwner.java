package com.bloom.bloomschool.biometrics.service;

import com.bloom.bloomschool.attendance.util.OwnerType;

import java.util.UUID;

public record IdentifiedOwner(UUID bioDataUuid, OwnerType ownerType, double score) {
}
