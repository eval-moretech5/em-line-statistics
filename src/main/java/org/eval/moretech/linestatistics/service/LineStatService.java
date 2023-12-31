package org.eval.moretech.linestatistics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LinkedMap;
import org.eval.moretech.linestatistics.entity.*;
import org.eval.moretech.linestatistics.repository.LineStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineStatService {

    private static final int PREDICTION_INTERVAL_IN_MINUTES = 15;
    private static final int LAST_TIME_INTERVAL_FOR_NOW_PREDICTION_IN_MINUTES = 30;

    private static final int MAX_LINE_TIME_IN_SECONDS = 60 * 40;

    private final LineStatRepository lineStatRepository;

    @Transactional
    public void save(List<LineStat> stats) {
        lineStatRepository.saveAll(stats);
    }

    @Transactional
    public LineTimeResponse findAverageLineTimeForLastHour(Long placeId, PersonType personType) {

        log.info("date to search {}", LocalDateTime.now().minusMinutes(LAST_TIME_INTERVAL_FOR_NOW_PREDICTION_IN_MINUTES));

        AverageLineStat result = lineStatRepository.findAverageLineTimeForLastMinutes(
            placeId, personType,
            LocalDateTime.now().minusMinutes(LAST_TIME_INTERVAL_FOR_NOW_PREDICTION_IN_MINUTES),
            LocalDateTime.now()
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

    public Map<String, Integer> findAverageLineTimeStatForDay(Long placeId, PersonType personType, LocalDate date) {
        //Какая-то сраная ошибка с маппингом. В запросе выше получилось сделать, тут - нет. Я зае**лся, так и не решил.
        List<Object[]> stats = lineStatRepository.findAverageLineTimeStatForDay(
            placeId, personType, date.atStartOfDay(), date.plusDays(1).atStartOfDay()
        );

        Map<String, Integer> statMap = new LinkedMap<>();
        for (Object[] stat : stats) {
            statMap.put(
                String.valueOf(((Double) stat[0]).intValue()),
                ((Double) stat[1]).intValue()
            );
        }
        return statMap;
    }
}
