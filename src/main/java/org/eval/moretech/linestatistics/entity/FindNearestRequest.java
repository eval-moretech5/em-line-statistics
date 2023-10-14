package org.eval.moretech.linestatistics.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindNearestRequest {
    private PointDto from;
    private PersonType personType;
    private Integer radius;
    private Integer limit;
}
