package com.featureflags.service.impl;

import com.featureflags.config.ModelMappingConfig;
import com.featureflags.data.entity.FeatureFlag;
import com.featureflags.data.repository.FeatureFlagRepository;
import com.featureflags.dto.CreateFeatureFlagDto;
import com.featureflags.dto.FeatureFlagDto;
import com.featureflags.dto.FlagEvaluationDto;
import com.featureflags.dto.UpdateFeatureFlagDto;
import com.featureflags.exception.DuplicateFlagNameException;
import com.featureflags.exception.FlagNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FeatureFlagServiceImplTest {

    @Mock
    private FeatureFlagRepository featureFlagRepository;

    @Spy
    private ModelMappingConfig modelMappingConfig = new ModelMappingConfig();

    @InjectMocks
    private FeatureFlagServiceImpl featureFlagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFlag_duplicateName_throwsDuplicateException() {
        CreateFeatureFlagDto createFeatureFlagDto = CreateFeatureFlagDto.builder()
                .name("existing-flag")
                .build();

        when(featureFlagRepository.existsByName("existing-flag")).thenReturn(true);

        assertThrows(DuplicateFlagNameException.class, () -> featureFlagService.createFlag(createFeatureFlagDto));
        verify(featureFlagRepository, never()).save(any());
    }

    @Test
    void getFlagById_nonExistentId_throwsNotFoundException() {
        when(featureFlagRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FlagNotFoundException.class, () -> featureFlagService.getFlagById(99L));
    }

    @Test
    void updateFlag_toggleEnabled_onlyChangesEnabled() {
        FeatureFlag existing = buildFlag(1L, "dark-mode", "Dark mode.", false);
        FeatureFlag saved = buildFlag(1L, "dark-mode", "Dark mode.", true);

        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenReturn(saved);

        UpdateFeatureFlagDto updateDto = UpdateFeatureFlagDto.builder()
                .enabled(true)
                .build();

        FeatureFlagDto result = featureFlagService.updateFlag(1L, updateDto);

        assertTrue(result.isEnabled());
        assertEquals("dark-mode", result.getName());
        assertEquals("Dark mode.", result.getDescription());
    }

    @Test
    void evaluateFlag_existingEnabledFlag_returnsTrueEvaluation() {
        FeatureFlag flag = buildFlag(1L, "dark-mode", "Dark mode.", true);
        when(featureFlagRepository.findByName("dark-mode")).thenReturn(Optional.of(flag));

        FlagEvaluationDto result = featureFlagService.evaluateFlag("dark-mode");

        assertEquals("dark-mode", result.getName());
        assertTrue(result.isEnabled());
    }

    private FeatureFlag buildFlag(long id, String name, String description, boolean enabled) {
        FeatureFlag flag = FeatureFlag.builder()
                .name(name)
                .description(description)
                .enabled(enabled)
                .build();
        flag.setId(id);
        flag.setCreatedAt(LocalDateTime.now());
        flag.setUpdatedAt(LocalDateTime.now());
        return flag;
    }


}
