package de.hskl.rateme.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.hskl.rateme.dao.PoiDao;
import de.hskl.rateme.dto.PoiDetailDto;
import de.hskl.rateme.dto.PoiOverviewDto;
import de.hskl.rateme.entity.Poi;

@Service
public class PoiService {

    private final PoiDao poiDao;

    public PoiService(PoiDao poiDao) {
        this.poiDao = poiDao;
    }

    public List<PoiOverviewDto> findAllPois() {
        return poiDao.findAll()
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
        Poi poi = poiDao.findById(id)
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
