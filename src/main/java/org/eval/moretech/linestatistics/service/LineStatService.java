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
    private final LineStatRepository lineStatRepository;

    @Transactional
    public void save(List<LineStat> stats) {
        lineStatRepository.saveAll(stats);
//        lineStatRepository.flush();
    }

    @Transactional
    public LineStat save(LineStat stat) {
        LineStat stat1 = lineStatRepository.save(stat);
        lineStatRepository.flush();
        return stat1;
    }

    @Transactional
    public LineTimeResponse findAverageLineTimeForLastHour(Long placeId, PersonType personType) {

        log.info("date to search {}", LocalDateTime.now().minus(1, ChronoUnit.HOURS));

        List<AverageLineStat> result = lineStatRepository.findAverageLineTimeForLastHour(
            placeId, personType, LocalDateTime.now().minus(1, ChronoUnit.HOURS)
        );
        return LineTimeResponse.builder()
            .lineTime(
                result.get(0).getLine().intValue()
            )
            .build();
    }
}
