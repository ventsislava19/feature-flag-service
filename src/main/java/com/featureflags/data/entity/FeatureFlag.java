package com.featureflags.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feature_flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlag extends BaseEntity{

    @NotBlank(message = "Flag name must not be blank.")
    @Size(min = 2, max = 50, message = "Flag name must be btw 2 and 50 chars.")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Flag name must be kebab-case.")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 150, message = "Description must not be more than 150 chars.")
    @Column(length = 150)
    private String description;

    @Column(nullable = false)
    @Builder.Default // Ensure to not be set to null, when enabled isn't set.
    private boolean enabled = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
