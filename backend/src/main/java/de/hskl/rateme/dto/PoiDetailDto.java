package de.hskl.rateme.dto;

public record PoiDetailDto(
                Long id,
                String name,
                String type,
                String amenity,
                String cuisine,
                String phone,
                String website,
                Double lat,
                Double lon,
                String openingHours,
                String wheelchair,
                String takeaway,
                String delivery,
                String smoking,
                String outdoorSeating,
                String reservation,
                String addrCity,
                String addrCountry,
                String addrHousenumber,
                String addrPostcode,
                String addrStreet) {

}
