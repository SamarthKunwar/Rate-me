package de.hskl.rateme.dto;

import java.time.LocalDateTime;

public record RatingDtoOut(Integer id,
                String username,
                Integer grade,
                String text,
                LocalDateTime createdAt,
                boolean hasImage) {

}
