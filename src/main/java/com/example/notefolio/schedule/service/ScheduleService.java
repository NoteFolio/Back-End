package com.example.notefolio.schedule.service;

import com.example.notefolio.schedule.domain.Category;
import com.example.notefolio.schedule.domain.Schedule;
import com.example.notefolio.schedule.dto.request.ScheduleRequest;
import com.example.notefolio.schedule.dto.response.ApiMessageResponse;
import com.example.notefolio.schedule.dto.response.ScheduleResponse;
import com.example.notefolio.schedule.repository.CategoryRepository;
import com.example.notefolio.schedule.repository.ScheduleRepository;
import com.nimbusds.oauth2.sdk.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;

    /*
     * 할 일 생성
     * categoryRepository.findById() 메서드를 사용하여 request의 categoryId에 해당하는 Category를 데이터베이스에서 조회한다.
     * orElseThrow()를 사용해 만약 해당하는 카테고리가 없으면 IllegalArgumentException을 발생시켜 예외를 처리한다.
     * Schedule.of() 정적 팩토리 메서드를 호출하여 request와 catetory 정보를 바탕으로 새로운 Task 객체를 생성하여 task 변수에 할당한다.
     * scheduleRepository.save() 메서드를 통해 생성된 task 객체를 데이터베이스에 저장한 후 반환된 객체는 saved 변수에 할당한다.
     * ScheduleResponse.from() 메서드를 호출하여 저장된 saved 객체를 기반으로 클라이언트에게 전달할 최종 응답 객체인 TaskResponse를 생성하고 반환한다.
     */
    public ScheduleResponse createTask(ScheduleRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        Schedule schedule = Schedule.of(request, category);
        Schedule saved = scheduleRepository.save(schedule);

        return ScheduleResponse.from(saved);
    }

    //일정 조회
    @Transactional(readOnly = true)
    public ScheduleResponse findTask(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정을 찾을 수 없습니다."));

        return ScheduleResponse.from(schedule);
    }

    //일정 수정
    @Transactional
    public ScheduleResponse amendSchedule(Long scheduleId, Long userId, ScheduleRequest request) {
        //소유자 검증 포함 조회
        Schedule schedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "일정을 확인할 수 없습니다."));

        //카테고리 변경 처리(null이면 변경 안함)
        Category newCategory = null;

        if(request.getCategoryId() != null) {
            newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리를 찾을 수 없습니다."));
        }

        //시간 유효성 체크
        var newStart = (request.getStartDateTime() != null) ? request.getStartDateTime() : schedule.getStartDateTime();
        var newEnd   = (request.getEndDateTime() != null)   ? request.getEndDateTime()   : schedule.getEndDateTime();
        if (newStart != null && newEnd != null && !newStart.isBefore(newEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시작시간은 종료시간보다 빨라야 합니다.");
        }

        //alertBefore 도메인 제약 검증
        if (request.getAlertBefore() != null && (request.getAlertBefore() < 0 || request.getAlertBefore() > 24 * 60)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "알림 분(min)은 0~1440 사이여야 합니다.");
        }

        //도메인 변경 (엔티티 메서드로 캡슐화)
        schedule.update(request, newCategory);

        //저장 (영속 상태면 dirty checking으로 flush)
        Schedule saved = scheduleRepository.save(schedule);

        //응답 DTO 변환
        return ScheduleResponse.from(saved);
    }

    //일정 삭제
    @Transactional
    public ApiMessageResponse deleteSchedule(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "일정을 확인할 수 없습니다."));

        scheduleRepository.delete(schedule);

        return ApiMessageResponse.of("일정이 삭제되었습니다.");
    }

}
