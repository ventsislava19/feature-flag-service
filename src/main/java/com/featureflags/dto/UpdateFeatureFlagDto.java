package com.featureflags.dto;

// For the PATCH /.../flags/{id}.

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeatureFlagDto {

    @Size(min = 2, max = 50, message = "Flag name must be btw 2 and 50 chars.")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Flag name must be kebab-case.")
    private String name;

    @Size(max = 150, message = "Description must not be more than 150 chars.")
    private String description;

    @Builder.Default
    private Boolean enabled = false;
}
