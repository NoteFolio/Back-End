package com.example.notefolio.schedule.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Builder
@Getter
public class ScheduleRequest {

    private Long userId; //User ID

    @NotBlank(message = "제목은 필수입니다.")
    private String title; //제목

    @Size(max = 300)
    private String description; //설명

    private LocalDateTime startDateTime; //시작날짜

    private LocalDateTime endDateTime; //종료날짜

    private Long categoryId; //카테고리 ID

    private Boolean isCompleted; //알림여부

    private Integer alertBefore; //몇 분전에 알림을 받을지
}
