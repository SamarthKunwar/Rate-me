package de.hskl.rateme.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RatingDtoOut createRating(
            @RequestHeader("Authorization") String token,
            @RequestBody RatingDtoIn request) {
        return ratingService.createRating(token, request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RatingDtoOut createRatingWithImage(
            @RequestHeader("Authorization") String token,
            @RequestParam Long poiId,
            @RequestParam Integer grade,
            @RequestParam String text,
            @RequestParam(required = false) MultipartFile image) {
        return ratingService.createRatingWithImage(token, poiId, grade, text, image);
    }

    @GetMapping("/me")
    public List<MyRatingDtoOut> getMyRatings(@RequestHeader("Authorization") String token) {
        return ratingService.getMyRatings(token);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RatingDtoOut updateRating(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestBody RatingDtoIn request) {
        return ratingService.updateRating(token, id, request);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RatingDtoOut updateRatingWithImage(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestParam Integer grade,
            @RequestParam String text,
            @RequestParam(required = false) MultipartFile image) {
        return ratingService.updateRatingWithImage(token, id, grade, text, image);
    }

    @DeleteMapping("/{id}")
    public void deleteRating(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {
        ratingService.deleteRating(token, id);
    }
}
