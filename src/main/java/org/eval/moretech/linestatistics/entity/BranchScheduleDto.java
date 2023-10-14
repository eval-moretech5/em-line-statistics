package org.eval.moretech.linestatistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class BranchScheduleDto {
    private Long id;

    private Map<PersonType, ScheduleWeek> schedules;

    public static class ScheduleWeek extends HashMap<DayOfWeek, ScheduleDay> {
    }

    @Data
    public static class ScheduleDay {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime from;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime till;
    }
}
