package org.eval.moretech.linestatistics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eval.moretech.linestatistics.entity.BranchScheduleDto;
import org.eval.moretech.linestatistics.entity.FindNearestRequest;
import org.eval.moretech.linestatistics.entity.LineStat;
import org.eval.moretech.linestatistics.entity.PersonType;
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
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineStatGenerationService {

    private static final Random random = new Random();

    private final LineStatService lineStatService;
    private final BranchFeignClient branchFeignClient;

//    private List<BranchScheduleDto> branches;

    @Async
    @Transactional
    public void generate() {

        List<BranchScheduleDto> branches = branchFeignClient.getAllBranches();

        LocalDate date = LocalDate.now().minusDays(15);

        do {
            generateForDay(branches, date);
            date = date.plusDays(1);
        } while (date.isBefore(LocalDate.now()));

        generateForLastMinutes(branches, true);

    }

    @Async
    @Transactional
    public void generate(FindNearestRequest request) {

        List<BranchScheduleDto> branches = branchFeignClient.getFilteredBranches(request);

        LocalDate date = LocalDate.now().minusDays(15);

        do {
            generateForDay(branches, date);
            date = date.plusDays(1);
        } while (date.isBefore(LocalDate.now()));

        generateForLastMinutes(branches, true);

    }

    protected void generateForDay(List<BranchScheduleDto> branches, LocalDate date) {
        log.info("Generation started for {}", date);

        for (BranchScheduleDto branch : branches) {

            for (Map.Entry<PersonType, BranchScheduleDto.ScheduleWeek> personTypeWeek: branch.getSchedules().entrySet()) {

                if (personTypeWeek.getValue().containsKey(date.getDayOfWeek())) {

                    BranchScheduleDto.ScheduleDay daySchedule = personTypeWeek.getValue().get(date.getDayOfWeek());
                    if (daySchedule == null) {
                        continue;
                    }

                    generateForDayAndBranch(
                        branch.getId(),
                        personTypeWeek.getKey(),
                        date,
                        daySchedule.getFrom(),
                        daySchedule.getTill()
                    );

                }
            }
        }
        log.info("Generation completed for {}", date);
    }

//    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void scheduledGeneration() {

        List<BranchScheduleDto> branches = branchFeignClient.getAllBranches();

        if (CollectionUtils.isEmpty(branches)) {
            return;
        }
        generateForLastMinutes(branches, false);
    }

    private void generateForLastMinutes(List<BranchScheduleDto> branches, boolean fromStartOfADay) {
        LocalDate today = LocalDate.now();
        LocalTime till = LocalTime.now();
        log.info("Generation started for today {} till {}", today, till);

        for (BranchScheduleDto branch : branches) {

            for (Map.Entry<PersonType, BranchScheduleDto.ScheduleWeek> personTypeWeek: branch.getSchedules().entrySet()) {

                if (personTypeWeek.getValue().containsKey(today.getDayOfWeek())) {

                    BranchScheduleDto.ScheduleDay daySchedule = personTypeWeek.getValue().get(today.getDayOfWeek());

                    if (daySchedule == null) {
                        continue;
                    }

                    LocalTime from;
                    if (fromStartOfADay) {
                        from = daySchedule.getFrom();
                    } else {
                        from = till.minusMinutes(10);
                        if (from.isBefore(daySchedule.getFrom())) {
                            continue;
                        }
                    }

                    generateForDayAndBranch(
                        branch.getId(),
                        personTypeWeek.getKey(),
                        today,
                        from,
                        till
                    );

                }
            }
        }

        log.info("Generation completed for today {} till {}", today, till);
    }

    protected void generateForDayAndBranch(Long placeId, PersonType personType, LocalDate date, LocalTime from, LocalTime till) {

        if (from == null || till == null) {
            return;
        }

        LocalTime time = from;

        while (time.isBefore(till)) {

            LocalDateTime dateTime = LocalDateTime.of(date, time);

            LineStat stat = LineStat.builder()
                .placeId(placeId)
                .personType(personType)
                .createdAt(dateTime)
                .lineTime(random.nextInt(60 * 40))
                .build();

            lineStatService.save(stat);

            time = time.plusMinutes(10);
        }

        log.info("Saved stats for place {}, personType {}, date {}, day {}"/*, stats.size()*/, placeId, personType, date, date.getDayOfWeek());
    }

}
