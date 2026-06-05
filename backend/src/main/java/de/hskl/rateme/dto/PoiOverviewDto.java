package de.hskl.rateme.dto;

public record PoiOverviewDto(
        Long id,
        String name,
        Double lat,
        Double lon,
        String amenity) {

}
