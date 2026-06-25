package com.featureflags.controller.api;

import com.featureflags.dto.CreateFeatureFlagDto;
import com.featureflags.dto.FeatureFlagDto;
import com.featureflags.dto.FlagEvaluationDto;
import com.featureflags.dto.UpdateFeatureFlagDto;
import com.featureflags.service.FeatureFlagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flags")
@AllArgsConstructor
public class FeatureFlagApiController {

    private final FeatureFlagService featureFlagService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeatureFlagDto createFlag(@RequestBody @Valid CreateFeatureFlagDto createFeatureFlagDto) {
        return featureFlagService.createFlag(createFeatureFlagDto);
    }

    @GetMapping
    public List<FeatureFlagDto> getAllFlags() {
        return featureFlagService.getAllFlags();
    }

    @GetMapping("/{id}")
    public FeatureFlagDto getFlagById(@PathVariable long id) {
        return featureFlagService.getFlagById(id);
    }

    @PatchMapping("/{id}")
    public FeatureFlagDto updateFlag(@PathVariable long id, @RequestBody @Valid UpdateFeatureFlagDto updateFeatureFlagDto) {
        return featureFlagService.updateFlag(id, updateFeatureFlagDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlag(@PathVariable long id) {
        featureFlagService.deleteFlag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/evaluate")
    public FlagEvaluationDto evaluateFlag(@PathVariable String name) {
        return featureFlagService.evaluateFlag(name);
    }

}
