package de.hskl.rateme.dto;

import java.time.LocalDateTime;

public record MyRatingDto(Integer id,
        Long poiId,
        String poiName,
        Integer grade,
        String text,
        LocalDateTime createdAt,
        boolean hasImage) {

}
