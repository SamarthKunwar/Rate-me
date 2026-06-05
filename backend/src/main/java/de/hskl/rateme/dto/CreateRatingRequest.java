package de.hskl.rateme.dto;

public record CreateRatingRequest(
        Long poiId,
        Integer grade,
        String text) {

}
