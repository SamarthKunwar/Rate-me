package de.hskl.rateme.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hskl.rateme.dto.PoiDetailDtoOut;
import de.hskl.rateme.dto.PoiOverviewDtoOut;
import de.hskl.rateme.service.PoiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pois")
@Tag(name = "POIs", description = "Read points of interest")

public class PoiController {

    private final PoiService poiService;

    public PoiController(PoiService poiService) {
        this.poiService = poiService;
    }

    @Operation(summary = "Get all POIs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "POIs loaded successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    @GetMapping
    public List<PoiOverviewDtoOut> getAllPois(@RequestHeader("Authorization") String token) {
        return poiService.findAllPois(token);
    }

    @Operation(summary = "Get one POI by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "POI found"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @GetMapping("/{id}")
    public PoiDetailDtoOut getOnePoi(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return poiService.findPoiById(id, token);
    }

}
