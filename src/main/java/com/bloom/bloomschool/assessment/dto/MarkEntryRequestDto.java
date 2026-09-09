package com.bloom.bloomschool.assessment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
public class MarkEntryRequestDto {
    @NotEmpty @Valid
    private List<Entry> entries;

    @Data
    public static class Entry{
        @NonNull private UUID studentUuid;
        private Double score;
    }

}
