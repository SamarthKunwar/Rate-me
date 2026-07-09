package de.hskl.rateme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hskl.rateme.dto.RatingDtoIn;
import de.hskl.rateme.dto.RatingDtoOut;
import de.hskl.rateme.service.RatingService;

class RatingControllerTest {

    private RatingService ratingService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ratingService = mock(RatingService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RatingController(ratingService)).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void createRatingReturnsCreated() throws Exception {
        RatingDtoIn request = new RatingDtoIn(332050722L, 5, "Very good food.");
        RatingDtoOut response = new RatingDtoOut(
                1,
                "testuser",
                5,
                "Very good food.",
                LocalDateTime.of(2026, 6, 16, 12, 0),
                null,
                false);

        when(ratingService.createRating(eq("token-123"), any(RatingDtoIn.class))).thenReturn(response);

        mockMvc.perform(post("/ratings")
                .header("Authorization", "token-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.grade").value(5))
                .andExpect(jsonPath("$.hasImage").value(false));
    }

    @Test
    void updateRatingReturnsOk() throws Exception {
        RatingDtoIn request = new RatingDtoIn(332050722L, 4, "Updated text.");
        RatingDtoOut response = new RatingDtoOut(
                1,
                "testuser",
                4,
                "Updated text.",
                LocalDateTime.of(2026, 6, 16, 12, 0),
                null,
                false);

        when(ratingService.updateRating(eq("token-123"), eq(1), any(RatingDtoIn.class))).thenReturn(response);

        mockMvc.perform(put("/ratings/1")
                .header("Authorization", "token-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(4))
                .andExpect(jsonPath("$.text").value("Updated text."));
    }

    @Test
    void deleteRatingReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/ratings/1")
                .header("Authorization", "token-123"))
                .andExpect(status().isNoContent());

        verify(ratingService).deleteRating(eq("token-123"), eq(1));
    }
}
