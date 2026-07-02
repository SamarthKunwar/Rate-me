package de.hskl.rateme.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import de.hskl.rateme.entity.Poi;
import de.hskl.rateme.entity.Rating;
import de.hskl.rateme.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RatingDaoDatabaseTest {

    private final PoiDao poiDao;
    private final RatingDao ratingDao;
    private final UserDao userDao;

    @Autowired
    RatingDaoDatabaseTest(PoiDao poiDao, RatingDao ratingDao, UserDao userDao) {
        this.poiDao = poiDao;
        this.ratingDao = ratingDao;
        this.userDao = userDao;
    }

    @Test
    void ratingCanBeCreatedLoadedUpdatedAndDeleted() {
        Poi poi = poiDao.findById(332050722L).orElseThrow();
        User user = userDao.create(createTestUser());

        Rating createdRating = ratingDao.create(new Rating(user, poi, 5, "Database test rating.", null));

        assertNotNull(createdRating.getId());
        assertNotNull(createdRating.getCreatedAt());

        List<Rating> ratingsForPoi = ratingDao.findByPoiId(poi.getId());
        assertTrue(ratingsForPoi.stream().anyMatch(rating -> rating.getId().equals(createdRating.getId())));

        List<Rating> ratingsForUser = ratingDao.findByUserId(user.getId());
        assertEquals(1, ratingsForUser.size());
        assertEquals(createdRating.getId(), ratingsForUser.getFirst().getId());

        Optional<Rating> ratingWithDetails = ratingDao.findByIdWithDetails(createdRating.getId());
        assertTrue(ratingWithDetails.isPresent());
        assertEquals(user.getUsername(), ratingWithDetails.get().getUser().getUsername());
        assertEquals(poi.getId(), ratingWithDetails.get().getPoi().getId());

        createdRating.update(3, "Updated database test rating.", null);
        Rating updatedRating = ratingDao.update(createdRating);

        assertEquals(3, updatedRating.getGrade());
        assertEquals("Updated database test rating.", updatedRating.getText());

        ratingDao.delete(updatedRating);

        assertFalse(ratingDao.findById(createdRating.getId()).isPresent());
    }

    private User createTestUser() {
        String username = "db" + UUID.randomUUID().toString().substring(0, 8);

        return new User(
                username,
                username + "@example.com",
                "Test",
                "User",
                "Teststrasse",
                "1",
                "66482",
                "Zweibruecken",
                new byte[] { 1, 2, 3 },
                new byte[] { 4, 5, 6 });
    }
}
