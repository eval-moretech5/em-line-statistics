package org.eval.moretech.linestatistics.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class AverageDayLineStat {
    private final Double hour;
    private final Double line;
}
