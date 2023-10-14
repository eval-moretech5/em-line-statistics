package org.eval.moretech.linestatistics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eval.moretech.linestatistics.entity.*;
import org.eval.moretech.linestatistics.feign.BranchFeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineStatGenerationService {

    private static final int MAX_LINE_TIME_IN_SECONDS = 60 * 40;
    private static final int STAT_GENERATION_INTERVAL_IN_MINUTES = 10;

    private static final Random random = new Random();

    private final LineStatService lineStatService;
    private final BranchFeignClient branchFeignClient;

    @Async
    @Transactional
    public void generate(GenerateStatisticRequest request) {

        FindNearestRequest branchesRequest = FindNearestRequest.builder()
            .from(request.getFrom())
            .personType(request.getPersonType())
            .radius(request.getRadius())
            .limit(request.getLimit())
            .build();

        List<BranchScheduleDto> branches;
        if (request.getFrom() != null) {
            branches = branchFeignClient.getFilteredBranches(branchesRequest);
        } else {
            branches = branchFeignClient.getAllBranches();
        }

        LocalDate till = request.getTill();
        if (till == null) {
            till = LocalDate.now();
        }

        LocalDate date = till.minusDays(request.getDaysBefore());

        do {
            List<LineStat> stats = generateForDay(branches, date);
            lineStatService.save(stats);
            log.info("Generation completed for {}. Saved {} items", date, stats.size());
            date = date.plusDays(1);
        } while (date.isBefore(till));
    }

    protected List<LineStat> generateForDay(List<BranchScheduleDto> branches, LocalDate date) {
        List<LineStat> stats = new ArrayList<>();

        for (BranchScheduleDto branch : branches) {

            for (Map.Entry<PersonType, BranchScheduleDto.ScheduleWeek> personTypeWeek: branch.getSchedules().entrySet()) {

                if (personTypeWeek.getValue().containsKey(date.getDayOfWeek())) {

                    BranchScheduleDto.ScheduleDay daySchedule = personTypeWeek.getValue().get(date.getDayOfWeek());
                    if (daySchedule == null) {
                        continue;
                    }

                    List<LineStat> patch = generateForDayAndBranch(
                        branch.getId(),
                        personTypeWeek.getKey(),
                        date,
                        daySchedule.getFrom(),
                        daySchedule.getTill()
                    );

                    stats.addAll(patch);

                }
            }
        }
        return stats;
    }

    protected List<LineStat> generateForDayAndBranch(Long placeId, PersonType personType, LocalDate date, LocalTime from, LocalTime till) {
        List<LineStat> stats = new ArrayList<>();

        if (from == null || till == null) {
            return List.of();
        }

        LocalTime time = from;

        while (time.isBefore(till)) {

            LocalDateTime dateTime = LocalDateTime.of(date, time);

            LineStat stat = LineStat.builder()
                .placeId(placeId)
                .personType(personType)
                .createdAt(dateTime)
                .lineTime(random.nextInt(MAX_LINE_TIME_IN_SECONDS))
                .build();

            stats.add(stat);

            time = time.plusMinutes(STAT_GENERATION_INTERVAL_IN_MINUTES);
        }

        return stats;
    }

}
