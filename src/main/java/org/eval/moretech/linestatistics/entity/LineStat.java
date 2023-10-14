package org.eval.moretech.linestatistics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "linestat", name = "stat")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "place_id")
    private Long placeId;
    @Column(name = "person_type")
    @Enumerated(EnumType.STRING)
    private PersonType personType;
    @Column(name = "line_time")
    private Integer lineTime;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
