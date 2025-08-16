package com.example.notefolio.schedule.dto.response;

import com.example.notefolio.schedule.dto.CategoryDto;
import com.example.notefolio.schedule.domain.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ScheduleResponse {
    private Long id; //Task ID
    private String title; //제목
    private String description; //설명
    private LocalDateTime startDateTime; //시작날짜
    private LocalDateTime endDateTime; //종료날짜
    private Boolean isCompleted; //알림여부
    private CategoryDto category; //카테고리
    private Integer alertBefore; // 몇 분전에 알림을 받을지

    //Task를 TaskResponse 객체로 바꿔주는 메서드
    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getStartDateTime(),
                schedule.getEndDateTime(),
                schedule.getIsCompleted(),
                CategoryDto.from(schedule.getCategory()),
                schedule.getAlertBefore()
        );
    }
}
