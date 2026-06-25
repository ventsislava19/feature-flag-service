package com.featureflags.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflags.dto.CreateFeatureFlagDto;
import com.featureflags.dto.FeatureFlagDto;
import com.featureflags.exception.FlagNotFoundException;
import com.featureflags.service.impl.FeatureFlagServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeatureFlagApiController.class)
public class FeatureFlagApiControllerTest {

    @MockitoBean
    private FeatureFlagServiceImpl featureFlagService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 6, 24, 12, 0);

    @Test
    void createFlag_validInput_returns201WithCreatedFlag() throws Exception {
        CreateFeatureFlagDto createDto = CreateFeatureFlagDto.builder()
                .name("new-checkout")
                .description("New checkout flow.")
                .enabled(false)
                .build();

        FeatureFlagDto response = buildFlagDto(1L, "new-checkout", "New checkout flow.", false);
        when(featureFlagService.createFlag(any())).thenReturn(response);

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("new-checkout")))
                .andExpect(jsonPath("$.enabled", is(false)));
    }

    @Test
    void createFlag_blankName_returns400() throws Exception {
        String invalidJson = """
                { "name": "", "description": "test" }
                """;

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteFlag_existingId_returns204() throws Exception {
        doNothing().when(featureFlagService).deleteFlag(1L);

        mockMvc.perform(delete("/api/flags/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void evaluateFlag_nonExistentName_returns404() throws Exception {
        when(featureFlagService.evaluateFlag("unknown"))
                .thenThrow(new FlagNotFoundException("Flag not found with name: unknown."));

        mockMvc.perform(get("/api/flags/{name}/evaluate", "unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    private FeatureFlagDto buildFlagDto(long id, String name, String description, boolean enabled) {
        return FeatureFlagDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .enabled(enabled)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }
}
