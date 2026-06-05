package de.hskl.rateme.dto;

public record PoiDetailDto(
        Long Id,
        String name,
        String amenity,
        double lat,
        double lon,
        String opening_hours,
        String wheelchair,
        String takeaway,
        String delivery,
        String smoking,
        String outdoorSeating,
        String reservation,
        String addrCity,
        String addrCountry,
        String addrHouseNumber,
        String addrPostcode,
        String addrStreet) {

}
