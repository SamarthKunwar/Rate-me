package de.hskl.rateme.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import de.hskl.rateme.entity.Poi;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PoiDaoDatabaseTest {

    private static final Long EXISTING_POI_ID = 332050722L;

    private final PoiDao poiDao;

    @Autowired
    PoiDaoDatabaseTest(PoiDao poiDao) {
        this.poiDao = poiDao;
    }

    @Test
    void findAllReturnsSeededPois() {
        List<Poi> pois = poiDao.findAll();

        assertFalse(pois.isEmpty());
        assertTrue(pois.stream().anyMatch(poi -> EXISTING_POI_ID.equals(poi.getId())));
    }

    @Test
    void findByIdReturnsExistingPoi() {
        Poi poi = poiDao.findById(EXISTING_POI_ID).orElseThrow();

        assertEquals(EXISTING_POI_ID, poi.getId());
        assertEquals("Chin Chin Vietnamese Street Food", poi.getName());
    }
}
