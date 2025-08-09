package com.example.notefolio.schedule.repository;

import com.example.notefolio.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 소유자 검증 포함 단건 조회
    Optional<Schedule> findByIdAndUserId(Long id, Long userId);

    // 소유자 검증 포함 단일 삭제, 영향 행 수 반환
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Schedule s where s.id = :id and s.userId = :userId")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}