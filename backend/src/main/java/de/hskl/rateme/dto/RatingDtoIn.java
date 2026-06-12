package de.hskl.rateme.dto;

public record RatingDtoIn(
        Long poiId,
        Integer grade,
        String text) {

}
