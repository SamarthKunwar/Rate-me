package de.hskl.rateme.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hskl.rateme.dto.PoiDetailDto;
import de.hskl.rateme.dto.PoiOverviewDto;
import de.hskl.rateme.service.PoiService;

@RestController
@RequestMapping("/pois")
public class PoiController {

    private final PoiService poiService;

    public PoiController(PoiService poiService) {
        this.poiService = poiService;
    }

    @GetMapping
    public List<PoiOverviewDto> getAllPois() {
        return poiService.findAllPois();
    }

    @GetMapping("/{id}")
    public PoiDetailDto getOnePoi(@PathVariable Long id) {
        return poiService.findPoiById(id);
    }
}
