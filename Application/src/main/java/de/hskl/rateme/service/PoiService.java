package de.hskl.rateme.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.hskl.rateme.auth.AuthTokenManager;
import de.hskl.rateme.dao.PoiDao;
import de.hskl.rateme.dto.PoiDetailDtoOut;
import de.hskl.rateme.dto.PoiOverviewDtoOut;
import de.hskl.rateme.entity.Poi;

@Service
public class PoiService {

    private final PoiDao poiDao;
    private final AuthTokenManager authTokenManager;

    public PoiService(PoiDao poiDao, AuthTokenManager authTokenManager) {
        this.poiDao = poiDao;
        this.authTokenManager = authTokenManager;
    }

    public List<PoiOverviewDtoOut> findAllPois(String token) {
        authTokenManager.requireValidToken(token);

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

    public PoiDetailDtoOut findPoiById(Long id, String token) {
        authTokenManager.requireValidToken(token);

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
