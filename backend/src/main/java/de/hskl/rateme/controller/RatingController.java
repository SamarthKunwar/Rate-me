package de.hskl.rateme.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hskl.rateme.dto.MyRatingDtoOut;
import de.hskl.rateme.dto.RatingDtoIn;
import de.hskl.rateme.dto.RatingDtoOut;
import de.hskl.rateme.service.RatingService;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/poi/{poiId}")
    public List<RatingDtoOut> getRatingsForPoi(@PathVariable Long poiId) {
        return ratingService.getRatingsForPoi(poiId);
    }

    @PostMapping
    public RatingDtoOut createRating(
            @RequestHeader("Authorization") String token,
            @RequestBody RatingDtoIn request) {
        return ratingService.createRating(token, request);
    }

    @GetMapping("/me")
    public List<MyRatingDtoOut> getMyRatings(@RequestHeader("Authorization") String token) {
        return ratingService.getMyRatings(token);
    }

    @PutMapping("/{id}")
    public RatingDtoOut updateRating(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestBody RatingDtoIn request) {
        return ratingService.updateRating(token, id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRating(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {
        ratingService.deleteRating(token, id);
    }
}
