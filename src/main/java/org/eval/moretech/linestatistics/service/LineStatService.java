package org.eval.moretech.linestatistics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eval.moretech.linestatistics.entity.AverageLineStat;
import org.eval.moretech.linestatistics.entity.LineStat;
import org.eval.moretech.linestatistics.entity.LineTimeResponse;
import org.eval.moretech.linestatistics.entity.PersonType;
import org.eval.moretech.linestatistics.repository.LineStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineStatService {

    private static final int PREDICTION_INTERVAL_IN_MINUTES = 15;

    private static final int MAX_LINE_TIME_IN_SECONDS = 60 * 40;

    private final LineStatRepository lineStatRepository;

    @Transactional
    public void save(List<LineStat> stats) {
        lineStatRepository.saveAll(stats);
    }

    @Transactional
    public LineTimeResponse findAverageLineTimeForLastHour(Long placeId, PersonType personType) {

        log.info("date to search {}", LocalDateTime.now().minus(1, ChronoUnit.HOURS));

        AverageLineStat result = lineStatRepository.findAverageLineTimeForLastHour(
            placeId, personType, LocalDateTime.now().minus(1, ChronoUnit.HOURS)
        );

        if (result == null || result.getLine() == null) {
            return LineTimeResponse.builder()
                .lineTime(MAX_LINE_TIME_IN_SECONDS)
                .build();
        }

        return LineTimeResponse.builder()
            .lineTime((int) Math.round(result.getLine()))
            .build();
    }

    @Transactional
    public LineTimeResponse findAverageLineTimeForPredicted(Long placeId, PersonType personType, LocalDateTime dateTime) {

        log.info("date to search {}", LocalDateTime.now().minus(1, ChronoUnit.HOURS));

        int minuteStart = dateTime.getMinute() / PREDICTION_INTERVAL_IN_MINUTES;
        int minuteEnd = minuteStart + PREDICTION_INTERVAL_IN_MINUTES;

        AverageLineStat result = lineStatRepository.findAverageLineTimeForPredicted(
            placeId, personType,
            dateTime.getDayOfWeek().getValue(), dateTime.getHour(), minuteStart, minuteEnd
        );

        if (result == null || result.getLine() == null) {
            return LineTimeResponse.builder()
                .lineTime(MAX_LINE_TIME_IN_SECONDS)
                .build();
        }

        return LineTimeResponse.builder()
            .lineTime((int) Math.round(result.getLine()))
            .build();
    }
}
