package com.example.notefolio.schedule.dto.response;

public record ApiMessageResponse(String message) {
    public static ApiMessageResponse of(String msg) { return new ApiMessageResponse(msg); }
}