package org.eval.moretech.linestatistics.repository;

import org.eval.moretech.linestatistics.entity.AverageLineStat;
import org.eval.moretech.linestatistics.entity.LineStat;
import org.eval.moretech.linestatistics.entity.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface LineStatRepository extends JpaRepository<LineStat, Long> {

    @Query(
        value = "select new org.eval.moretech.linestatistics.entity.AverageLineStat(AVG(s.lineTime)) average " +
            "from LineStat s " +
            "where s.placeId=:placeId and s.personType=:personType and s.createdAt > :time"
    )
    AverageLineStat findAverageLineTimeForLastHour(Long placeId, PersonType personType, LocalDateTime time);

    @Query(
        value = "select new org.eval.moretech.linestatistics.entity.AverageLineStat(AVG(s.lineTime)) average " +
            "from LineStat s " +
            "where " +
                "s.placeId=:placeId and " +
                "s.personType=:personType and " +
                "date_part('dow', s.createdAt) = :dow and " +
                "date_part('hour', s.createdAt) = :hour and " +
                "date_part('minute', s.createdAt) >= :minuteStart and " +
                "date_part('minute', s.createdAt) < :minuteEnd "
    )
    AverageLineStat findAverageLineTimeForPredicted(
        Long placeId, PersonType personType,
        Integer dow, Integer hour, Integer minuteStart, Integer minuteEnd
    );
}
