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
import com.featureflags.service.FeatureFlagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FeatureFlagServiceImpl implements FeatureFlagService {

    private final FeatureFlagRepository featureFlagRepository;
    private final ModelMappingConfig modelMappingConfig;

    @Override
    public FeatureFlagDto createFlag(@Valid CreateFeatureFlagDto createFeatureFlagDto) {
        if (featureFlagRepository.existsByName(createFeatureFlagDto.getName())) {
            throw new DuplicateFlagNameException("A flag with the following name already exists: " + createFeatureFlagDto.getName() + ".");
        }

        FeatureFlag featureFlag = modelMappingConfig.modelMapper().map(createFeatureFlagDto, FeatureFlag.class);
        FeatureFlag saved = featureFlagRepository.save(featureFlag);
        return modelMappingConfig.modelMapper().map(saved, FeatureFlagDto.class);
    }

    @Override
    public List<FeatureFlagDto> getAllFlags() {
        return modelMappingConfig.mapList(featureFlagRepository.findAll(), FeatureFlagDto.class);
    }

    @Override
    public FeatureFlagDto getFlagById(long id) {
        FeatureFlag featureFlag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FlagNotFoundException("Flag not found with id: " + id + "."));

        return modelMappingConfig.modelMapper().map(featureFlag, FeatureFlagDto.class);
    }

    @Override
    public FeatureFlagDto updateFlag(long id, @Valid UpdateFeatureFlagDto updateFeatureFlagDto) {
        FeatureFlag existing = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FlagNotFoundException("Flag not found with id: " + id + "."));

        if (updateFeatureFlagDto.getName() != null) {
            if (!existing.getName().equals(updateFeatureFlagDto.getName()) && featureFlagRepository.existsByName(updateFeatureFlagDto.getName())) {
                throw new DuplicateFlagNameException("A flag with the following name already exists: " + updateFeatureFlagDto.getName() + ".");
            }

            existing.setName(updateFeatureFlagDto.getName());
        }

        if (updateFeatureFlagDto.getDescription() != null) {
            existing.setDescription(updateFeatureFlagDto.getDescription());
        }

        if (updateFeatureFlagDto.getEnabled() != null) {
            existing.setEnabled(updateFeatureFlagDto.getEnabled());
        }

        FeatureFlag saved = featureFlagRepository.save(existing);
        return modelMappingConfig.modelMapper().map(saved, FeatureFlagDto.class);
    }

    @Override
    public void deleteFlag(long id) {
        if (!featureFlagRepository.existsById(id)) {
            throw new FlagNotFoundException("Flag not found with id: " + id + ".");
        }
        featureFlagRepository.deleteById(id);
    }

    @Override
    public FlagEvaluationDto evaluateFlag(String name) {
        FeatureFlag featureFlag = featureFlagRepository.findByName(name)
                .orElseThrow(() -> new FlagNotFoundException("Flag not found with name: " + name + "."));

        return FlagEvaluationDto.builder()
                .name(featureFlag.getName())
                .enabled(featureFlag.isEnabled())
                .build();
    }
}
