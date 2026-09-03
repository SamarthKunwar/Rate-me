package de.hskl.rateme.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.hskl.rateme.dao.PoiDao;
import de.hskl.rateme.dto.PoiDetailDtoOut;
import de.hskl.rateme.dto.PoiOverviewDtoOut;
import de.hskl.rateme.entity.Poi;

@Service
public class PoiService {

    private final PoiDao poiDao;

    public PoiService(PoiDao poiDao) {
        this.poiDao = poiDao;
    }

    // Public: the map and place details are visible without logging in.
    public List<PoiOverviewDtoOut> findAllPois() {
        return poiDao.findAll()
                .stream()
                .map(poi -> new PoiOverviewDtoOut(
                        poi.getId(),
                        poi.getName(),
                        poi.getLat(),
                        poi.getLon(),
                        poi.getAmenity()))
                .toList();
    }

    public PoiDetailDtoOut findPoiById(Long id) {
        Poi poi = poiDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return new PoiDetailDtoOut(
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
