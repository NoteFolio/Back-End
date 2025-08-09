package com.example.notefolio.schedule.domain;

import com.example.notefolio.schedule.dto.request.ScheduleRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Boolean isCompleted;

    private Integer alertBefore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Schedule(Long id, Long userId, String title, String description,
                    LocalDateTime startDateTime, LocalDateTime endDateTime,
                    Boolean isCompleted, Integer alertBefore, Category category) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isCompleted = isCompleted;
        this.alertBefore = alertBefore;
        this.category = category;
    }

    // req + category를 합쳐서 새로운 Task 객체 생성
    public static Schedule of(ScheduleRequest req, Category category) {
        return Schedule.builder()
                .userId(req.getUserId())
                .title(req.getTitle())
                .description(req.getDescription())
                .startDateTime(req.getStartDateTime())
                .endDateTime(req.getEndDateTime())
                .isCompleted(req.getIsCompleted())
                .alertBefore(req.getAlertBefore())
                .category(category)
                .build();
    }

    public void update(ScheduleRequest req, Category newCategory) {
        if (req.getTitle() != null) this.title = req.getTitle();
        if (req.getDescription() != null) this.description = req.getDescription();
        if (req.getStartDateTime() != null) this.startDateTime = req.getStartDateTime();
        if (req.getEndDateTime() != null) this.endDateTime = req.getEndDateTime();
        if (req.getIsCompleted() != null) this.isCompleted = req.getIsCompleted();
        if (req.getAlertBefore() != null) this.alertBefore = req.getAlertBefore();
        if (newCategory != null) this.category = newCategory;
    }
}

