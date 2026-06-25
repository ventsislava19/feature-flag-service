package com.featureflags.data.repository;

import com.featureflags.data.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {

    Optional<FeatureFlag> findByName(String name); // Look up by name.

    boolean existsByName(String name); // For duplicates.
}
