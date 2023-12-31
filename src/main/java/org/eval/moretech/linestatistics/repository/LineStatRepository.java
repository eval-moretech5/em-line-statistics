package org.eval.moretech.linestatistics.repository;

import org.eval.moretech.linestatistics.entity.AverageLineStat;
import org.eval.moretech.linestatistics.entity.LineStat;
import org.eval.moretech.linestatistics.entity.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LineStatRepository extends JpaRepository<LineStat, Long> {

    @Query(
        value = "select new org.eval.moretech.linestatistics.entity.AverageLineStat(AVG(s.lineTime)) average " +
            "from LineStat s " +
            "where s.placeId=:placeId and " +
            "s.personType=:personType and s.createdAt >= :fromTime and " +
            "s.personType=:personType and s.createdAt < :toTime "
    )
    AverageLineStat findAverageLineTimeForLastMinutes(Long placeId, PersonType personType,
                                                      LocalDateTime fromTime, LocalDateTime toTime);

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

    @Query(
        value = "select date_part('hour',s.createdAt) as hour, AVG(s.lineTime) as line " +
            "from LineStat s " +
            "where " +
                "s.placeId=:placeId and " +
                "s.personType=:personType and " +
                "s.createdAt >= :startDay and " +
                "s.createdAt < :endDay " +
            "group by date_part('hour',s.createdAt) " +
            "order by date_part('hour',s.createdAt)"
    )
    List<Object[]> findAverageLineTimeStatForDay(
        Long placeId, PersonType personType,
        LocalDateTime startDay, LocalDateTime endDay
    );
}
