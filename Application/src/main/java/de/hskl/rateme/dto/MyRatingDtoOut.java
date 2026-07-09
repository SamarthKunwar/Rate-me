package de.hskl.rateme.dto;

import java.time.LocalDateTime;

public record MyRatingDtoOut(Integer id,
        Long poiId,
        String poiName,
        Integer grade,
        String text,
        LocalDateTime createdAt,
        Integer imageId,
        boolean hasImage) {

}
