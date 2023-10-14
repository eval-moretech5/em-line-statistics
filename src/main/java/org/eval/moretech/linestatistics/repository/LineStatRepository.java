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
            "where s.placeId=:placeId and s.personType=:personType and s.createdAt > :time"
    )
    List<AverageLineStat> findAverageLineTimeForLastHour(Long placeId, PersonType personType, LocalDateTime time);
}
