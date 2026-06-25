package com.featureflags.data.repository;

import com.featureflags.data.entity.FeatureFlag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class FeatureFlagRepositoryTest {

    @Autowired
    private FeatureFlagRepository flagRepository;

    private FeatureFlag savedFlag;

    @BeforeEach
    void setUp() {
        flagRepository.deleteAll();

        FeatureFlag flag = FeatureFlag.builder()
                .name("dark-mode")
                .description("Enable dark mode UI.")
                .enabled(true)
                .build();
        savedFlag = flagRepository.save(flag);
    }

    @Test
    void findByName_existingName_returnsFlag() {
        Optional<FeatureFlag> result = flagRepository.findByName("dark-mode");

        assertTrue(result.isPresent());
        assertEquals("dark-mode", result.get().getName());
        assertEquals("Enable dark mode UI.", result.get().getDescription());
        assertTrue(result.get().isEnabled());
    }

    @Test
    void findByName_nonExistentName_returnsEmpty() {
        Optional<FeatureFlag> result = flagRepository.findByName("non-existent");

        assertFalse(result.isPresent());
    }

    @Test
    void findByName_existentName_returnsTrue() {
        assertTrue(flagRepository.existsByName("dark-mode"));
    }

    @Test
    void findByName_nonExistentName_returnsFalse() {
        assertFalse(flagRepository.existsByName("non-existent"));
    }

    @Test
    void save_duplicateName_throwsDataIntergrityViolation() {
        FeatureFlag duplicate = FeatureFlag.builder()
                .name("dark-mode")
                .description("Another dark mode flag.")
                .enabled(false)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            flagRepository.save(duplicate);
            flagRepository.flush();
        });
    }

    @Test
    void save_setsTimestampsAutomatically() {
        assertNotNull(savedFlag.getCreatedAt());
        assertNotNull(savedFlag.getUpdatedAt());
    }

    @Test
    void save_generatesId() {
        assertTrue(savedFlag.getId() > 0);
    }





}
