package org.eval.moretech.linestatistics.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class GenerateStatisticRequest {
    private PointDto from;
    private PersonType personType;
    private Integer radius;
    private Integer limit;
    private LocalDate till;
    private Integer daysBefore;
}
