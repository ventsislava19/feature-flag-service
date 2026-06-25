package com.featureflags.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// The response body used by most endpoints - shows all flag fields.

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagDto {

    private long id;
    private String name;
    private String description;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
