package com.bloom.bloomschool.assessments.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MarkEntryRequest {
    @NotEmpty
    @Valid
    private List<Entry> entries;

    @Data
    public static class Entry {
        @NotNull
        private UUID studentUuid;

        /** Null clears/leaves the mark ungraded. */
        private Double score;
    }
}
