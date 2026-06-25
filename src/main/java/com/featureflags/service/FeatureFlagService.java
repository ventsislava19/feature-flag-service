package com.featureflags.service;

import com.featureflags.dto.CreateFeatureFlagDto;
import com.featureflags.dto.FeatureFlagDto;
import com.featureflags.dto.FlagEvaluationDto;
import com.featureflags.dto.UpdateFeatureFlagDto;

import java.util.List;

public interface FeatureFlagService {

    // Will reject duplicates.
    FeatureFlagDto createFlag(CreateFeatureFlagDto createFeatureFlagDto);

    List<FeatureFlagDto> getAllFlags();

    // Either get by id or 404.
    FeatureFlagDto getFlagById(long id);

    // PATCH.
    FeatureFlagDto updateFlag(long id, UpdateFeatureFlagDto updateFeatureFlagDto);

    void deleteFlag(long id);

    // Checked if enabled by name or 404.
    FlagEvaluationDto evaluateFlag(String name);
}
