package org.eval.moretech.linestatistics.controller;

import lombok.RequiredArgsConstructor;
import org.eval.moretech.linestatistics.entity.LineTimeResponse;
import org.eval.moretech.linestatistics.entity.PersonType;
import org.eval.moretech.linestatistics.service.LineStatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/line")
@RequiredArgsConstructor
public class LineStatisticsController {

    private final LineStatService lineStatService;

    @GetMapping("/{placeId}/{personType}/now")
    LineTimeResponse getLineTimeForNow(@PathVariable Long placeId, @PathVariable PersonType personType) {
        return lineStatService.findAverageLineTimeForLastHour(placeId, personType);
    }

    @GetMapping("/{placeId}/{personType}/predict/{dateTime}")
    LineTimeResponse getLineTimeForFuture(
        @PathVariable Long placeId,
        @PathVariable PersonType personType,
        @PathVariable LocalDateTime dateTime
        ) {
        return lineStatService.findAverageLineTimeForPredicted(placeId, personType, dateTime);
    }
}
