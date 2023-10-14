package org.eval.moretech.linestatistics.entity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PointDto {
    Double lon;
    Double lat;
}
