package de.hskl.rateme.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.hskl.rateme.auth.AuthTokenManager;
import de.hskl.rateme.dao.PoiDao;
import de.hskl.rateme.dao.RatingDao;
import de.hskl.rateme.dto.MyRatingDtoOut;
import de.hskl.rateme.dto.RatingDtoIn;
import de.hskl.rateme.dto.RatingDtoOut;
import de.hskl.rateme.entity.Poi;
import de.hskl.rateme.entity.Rating;
import de.hskl.rateme.entity.User;

@Service
public class RatingService {

    private static final int MIN_GRADE = 0;
    private static final int MAX_GRADE = 5;
    private static final int MAX_TEXT_LENGTH = 2000;

    private final RatingDao ratingDao;
    private final PoiDao poiDao;
    private final AuthTokenManager authTokenManager;

    public RatingService(RatingDao ratingDao, PoiDao poiDao, AuthTokenManager authTokenManager) {
        this.ratingDao = ratingDao;
        this.poiDao = poiDao;
        this.authTokenManager = authTokenManager;
    }

    public List<RatingDtoOut> getRatingsForPoi(Long poiId) {
        return ratingDao.findByPoiId(poiId)
                .stream()
                .map(this::toRatingDtoOut)
                .toList();
    }

    public RatingDtoOut createRating(String token, RatingDtoIn request) {
        User user = getUserFromToken(token);
        validateRatingInput(request);

        Poi poi = poiDao.findById(request.poiId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "POI not found"));

        Rating rating = new Rating(user, poi, request.grade(), request.text(), null);
        Rating savedRating = ratingDao.create(rating);

        return toRatingDtoOut(savedRating);
    }

    public List<MyRatingDtoOut> getMyRatings(String token) {
        User user = getUserFromToken(token);

        return ratingDao.findByUserId(user.getId())
                .stream()
                .map(this::toMyRatingDtoOut)
                .toList();
    }

    public RatingDtoOut updateRating(String token, Integer ratingId, RatingDtoIn request) {
        User user = getUserFromToken(token);
        validateRatingInput(request);

        Rating rating = ratingDao.findByIdWithDetails(ratingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"));

        checkRatingOwner(rating, user);
        rating.update(request.grade(), request.text(), rating.getImage());

        Rating updatedRating = ratingDao.update(rating);
        return toRatingDtoOut(updatedRating);
    }

    public void deleteRating(String token, Integer ratingId) {
        User user = getUserFromToken(token);

        Rating rating = ratingDao.findByIdWithDetails(ratingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"));

        checkRatingOwner(rating, user);
        ratingDao.delete(rating);
    }

    private User getUserFromToken(String token) {
        return authTokenManager.getUser(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
    }

    private void validateRatingInput(RatingDtoIn request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating data is required");
        }

        Integer grade = request.grade();
        String text = request.text();

        if (grade == null || grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grade must be between 0 and 5");
        }

        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating text must not be empty");
        }

        if (text.length() > MAX_TEXT_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Rating text must not be longer than 2000 characters");
        }
    }

    private void checkRatingOwner(Rating rating, User user) {
        if (!Objects.equals(rating.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to modify this rating");
        }
    }

    private RatingDtoOut toRatingDtoOut(Rating rating) {
        return new RatingDtoOut(
                rating.getId(),
                rating.getUser().getUsername(),
                rating.getGrade(),
                rating.getText(),
                rating.getCreatedAt(),
                rating.getImage() != null);
    }

    private MyRatingDtoOut toMyRatingDtoOut(Rating rating) {
        return new MyRatingDtoOut(
                rating.getId(),
                rating.getPoi().getId(),
                rating.getPoi().getName(),
                rating.getGrade(),
                rating.getText(),
                rating.getCreatedAt(),
                rating.getImage() != null);
    }
}
