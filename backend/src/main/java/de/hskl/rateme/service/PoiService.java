package de.hskl.rateme.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.hskl.rateme.dataaccess.PoiDataAccess;
import de.hskl.rateme.entity.Poi;
import main.java.de.hskl.rateme.dto.PoiDetailDto;
import main.java.de.hskl.rateme.dto.PoiOverviewDto;

@Service
@Transactional
public class PoiService {

    private final PoiDataAccess poiDataAccess;

    public PoiService(PoiDataAccess poiDataAccess) {
        this.poiDataAccess = poiDataAccess;
    }

    public List<PoiOverviewDto> findAllPois() {
        return poiDataAccess.findAll()
                .stream()
                .map(poi -> new PoiOverviewDto(
                        poi.getId(),
                        poi.getName(),
                        poi.getLat(),
                        poi.getLon(),
                        poi.getAmenity()))
                .toList();
    }

    public PoiDetailDto findPoiById(Long id) {
        Poi poi = poiDataAccess.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return new PoiDetailDto(
                poi.getId(),
                poi.getName(),
                poi.getType(),
                poi.getAmenity(),
                poi.getCuisine(),
                poi.getPhone(),
                poi.getWebsite(),
                poi.getLat(),
                poi.getLon(),
                poi.getOpeningHours(),
                poi.getWheelchair(),
                poi.getTakeaway(),
                poi.getDelivery(),
                poi.getSmoking(),
                poi.getOutdoorSeating(),
                poi.getReservation(),
                poi.getAddrCity(),
                poi.getAddrCountry(),
                poi.getAddrHousenumber(),
                poi.getAddrPostcode(),
                poi.getAddrStreet());
    }

}
