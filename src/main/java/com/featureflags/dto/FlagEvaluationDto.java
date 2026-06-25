package com.featureflags.dto;

// For the GET /.../evaluate.

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlagEvaluationDto {

    private String name;
    private boolean enabled;
}
