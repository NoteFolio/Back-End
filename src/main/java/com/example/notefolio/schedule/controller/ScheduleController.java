package com.example.notefolio.schedule.controller;

import com.example.notefolio.schedule.dto.request.ScheduleRequest;
import com.example.notefolio.schedule.dto.response.ScheduleResponse;
import com.example.notefolio.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class ScheduleController {

    private final ScheduleService scheduleService;

    /*
    * 일정 생성
    * 요청 본문을 통해 받은 request 객체를 taskService로 넘겨 실제 할 일 생성 로직을 실행한다.
    * 메서드 실행이 성공적으로 끝나면 상태코드와 함께 TaskService가 반환한 TaskResponse 객체를 응답 본문에 담아 클라이언트에게 보낸다.
    */
    @PostMapping
    public ResponseEntity<ScheduleResponse> createTask(@RequestBody @Valid ScheduleRequest request) {
        ScheduleResponse response = scheduleService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //일정 조회
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> findTask(
            @PathVariable("id") Long scheduleId
    ){
        ScheduleResponse response = scheduleService.findTask(scheduleId);
        return ResponseEntity.ok(response);
    }

    //일정 수정
    @PatchMapping("/{id}") // 부분 수정이므로 PATCH 추천
    public ResponseEntity<ScheduleResponse> amendSchedule(
            @PathVariable("id") Long scheduleId,
            @RequestHeader("X-User-Id") Long userId,   // 임시 사용자 식별자 (인증 구현되면 수정해야함)
            @RequestBody @Valid ScheduleRequest req
    ) {
        ScheduleResponse res = scheduleService.amendSchedule(scheduleId, userId, req);
        return ResponseEntity.ok(res);
    }

    //일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") Long userId // 임시 사용자 식별자 (인증 구현되면 수정해야함)
    ) {
        scheduleService.deleteSchedule(id, userId);
        return ResponseEntity.noContent().build();
    }

}
