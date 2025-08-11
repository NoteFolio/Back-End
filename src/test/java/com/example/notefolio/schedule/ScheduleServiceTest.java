package com.example.notefolio.schedule;

import com.example.notefolio.schedule.domain.Category;
import com.example.notefolio.schedule.domain.Schedule;
import com.example.notefolio.schedule.dto.request.ScheduleRequest;
import com.example.notefolio.schedule.dto.response.ScheduleResponse;
import com.example.notefolio.schedule.repository.CategoryRepository;
import com.example.notefolio.schedule.repository.ScheduleRepository;
import com.example.notefolio.schedule.service.ScheduleService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any; //any(Task.class) 쓸 때 필요
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("일정 생성 - 성공")
    void createTask_success() {
        // given
        ScheduleRequest request = ScheduleRequest.builder()
                .userId(1L)
                .title("SQLD 기출문제 풀기")
                .description("1회차 복습")
                .startDateTime(LocalDateTime.of(2025, 7, 11, 13, 30))
                .endDateTime(LocalDateTime.of(2025, 7, 11, 15, 30))
                .categoryId(2L)
                .isCompleted(false)
                .alertBefore(30)
                .build();

        Category category = new Category(2L, "학습", "#FFA500");

        Schedule schedule = Schedule.of(request, category);

        given(categoryRepository.findById(2L)).willReturn(Optional.of(category));
        given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);

        // when
        ScheduleResponse response = scheduleService.createTask(request);

        // then
        assertEquals("SQLD 기출문제 풀기", response.getTitle());
        assertEquals(30, response.getAlertBefore());
        assertEquals("학습", response.getCategory().getName());

        /*
        * categoryrepo에서 2L을 조회하여 null이 아닌 category 객체를 포함하는 Optional 객체를 반환한다.
        * taskrepo에 Task.class 타입의 객체를 저장할건데 task를 반환해라
        * */
    }

    @Test
    @DisplayName("일정 조회 - 성공")
    void findTask_success() {
        //given
        Category category = new Category(1L,"학습", "#FFA500");

        Schedule schedule = Schedule.builder()
                .id(301L)
                .userId(1L)
                .title("SQLD 기출문제 풀기")
                .description("1회차 복습")
                .startDateTime(LocalDateTime.of(2025, 7,11, 13, 30))
                .endDateTime(LocalDateTime.of(2025,7,11,15,30))
                .isCompleted(false)
                .alertBefore(30)
                .category(category)
                .build();

        given(scheduleRepository.findById(301L)).willReturn(Optional.of(schedule));

        //when
        ScheduleResponse response = scheduleService.findTask(301L);

        //then
        assertEquals(301L, response.getId());
        assertEquals("SQLD 기출문제 풀기", response.getTitle());
        assertEquals("학습", response.getCategory().getName());
    }

    @Test
    @DisplayName("일정 수정 - 성공")
    void amendSchedule_success() {
        // Given
        Long userId = 1L;
        Long scheduleId = 301L;

        Category category = new Category(1L,"학습", "#FFA500");

        Schedule existing = Schedule.builder()
                .id(scheduleId)
                .userId(userId)
                .title("SQLD 기출문제 풀기")
                .description("1회차 복습")
                .startDateTime(LocalDateTime.of(2025, 7, 11, 13, 30))
                .endDateTime(LocalDateTime.of(2025, 7, 11, 15, 30))
                .isCompleted(false)
                .alertBefore(30)
                .category(category)
                .build();

        // 수정 요청 DTO (실서비스 시 Update용 Request를 따로 두는 걸 추천)
        ScheduleRequest req = ScheduleRequest.builder()
                .title("SQLD 기출문제 풀기")               // 동일
                .description("3회차 복습")                 // 변경
                .startDateTime(LocalDateTime.of(2025, 7, 13, 13, 30)) // 변경
                .endDateTime(LocalDateTime.of(2025, 7, 15, 15, 30))   // 변경
                .isCompleted(true)                         // 변경
                .alertBefore(10)                           // 변경
                .categoryId(1L)                            // 동일
                .build();

        // 소유자 검증 포함 조회
        given(scheduleRepository.findByIdAndUserId(scheduleId, userId))
                .willReturn(Optional.of(existing));

        // save는 넘긴 엔티티를 그대로 반환하도록 (일반적인 패턴)
        given(scheduleRepository.save(any(Schedule.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(categoryRepository.findById(1L))
                .willReturn(Optional.of(category));

        // When
        ScheduleResponse res = scheduleService.amendSchedule(scheduleId, userId, req);

        // Then
        assertEquals("3회차 복습", res.getDescription());
        assertEquals(LocalDateTime.of(2025, 7, 13, 13, 30), res.getStartDateTime());
        assertEquals(LocalDateTime.of(2025, 7, 15, 15, 30), res.getEndDateTime());
        assertEquals(Boolean.TRUE, res.getIsCompleted());
        assertEquals(10, res.getAlertBefore());

        // 레포 호출 검증
        verify(scheduleRepository).findByIdAndUserId(scheduleId, userId);
        verify(categoryRepository).findById(1L);
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    @DisplayName("일정 삭제 - 성공")
    void deleteSchedule_success() {
        //given
        Long userId = 1L;
        Long scheduleId = 301L;

        Category category = new Category(1L,"학습", "#FFA500");

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .userId(userId)
                .title("SQLD 기출문제 풀기")
                .category(category)
                .build();

        //소유자 검증
        given(scheduleRepository.findByIdAndUserId(scheduleId, userId))
                .willReturn(Optional.of(schedule));

        //when
        scheduleService.deleteSchedule(scheduleId, userId);

        //then
        verify(scheduleRepository).findByIdAndUserId(scheduleId, userId);
        verify(scheduleRepository).delete(schedule);      //실제 삭제 호출 검증
        verifyNoMoreInteractions(scheduleRepository);
    }
}

