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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.hskl.rateme.dto.MyRatingDtoOut;
import de.hskl.rateme.dto.RatingDtoIn;
import de.hskl.rateme.dto.RatingDtoOut;
import de.hskl.rateme.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ratings")
@Tag(name = "Ratings", description = "Create, read, update, and delete ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }
    @Operation(summary = "Get ratings for one POI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ratings loaded successfully")
    })
    @GetMapping("/poi/{poiId}")
    public List<RatingDtoOut> getRatingsForPoi(@PathVariable Long poiId) {
        return ratingService.getRatingsForPoi(poiId);
    }

    @Operation(summary = "Create a rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating data"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RatingDtoOut> createRating(
            @RequestHeader("Authorization") String token,
            @RequestBody RatingDtoIn request) {
        RatingDtoOut response = ratingService.createRating(token, request); // HTTP status: 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Body: RatingDtoOut JSON
    }

    @Operation(summary = "Create a rating with optional image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating or image data"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RatingDtoOut> createRatingWithImage(
            @RequestHeader("Authorization") String token,
            @RequestParam Long poiId,
            @RequestParam Integer grade,
            @RequestParam String text,
            @RequestParam(required = false) MultipartFile image) {
        RatingDtoOut response = ratingService.createRatingWithImage(token, poiId, grade, text, image); // HTTP status:
                                                                                                       // 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Body: RatingDtoOut JSON
    }

    @Operation(summary = "Get ratings of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ratings loaded successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    @GetMapping("/me")
    public List<MyRatingDtoOut> getMyRatings(@RequestHeader("Authorization") String token) {
        return ratingService.getMyRatings(token);
    }

    @Operation(summary = "Update a rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating data"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "403", description = "User is not owner of rating"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RatingDtoOut> updateRating(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestBody RatingDtoIn request) {
        RatingDtoOut response = ratingService.updateRating(token, id, request); // HTTP status: 200 OK
        return ResponseEntity.status(HttpStatus.OK).body(response); // Body: RatingDtoOut JSON
    }

    @Operation(summary = "Update a rating with optional image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating or image data"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "403", description = "User is not owner of rating"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RatingDtoOut> updateRatingWithImage(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestParam Integer grade,
            @RequestParam String text,
            @RequestParam(required = false) MultipartFile image) {
        RatingDtoOut response = ratingService.updateRatingWithImage(token, id, grade, text, image); // HTTP status: 200
                                                                                                    // OK
        return ResponseEntity.status(HttpStatus.OK).body(response); // Body: RatingDtoOut JSON
    }

    @Operation(summary = "Delete a rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rating deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "403", description = "User is not owner of rating"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {
        ratingService.deleteRating(token, id); // HTTP status: 204 No Content
        return ResponseEntity.noContent().build(); // Body: empty
    }
}
