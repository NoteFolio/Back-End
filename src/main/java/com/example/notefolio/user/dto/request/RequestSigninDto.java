package com.example.notefolio.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestSigninDto {
    private String email; // 사용자 이메일

    private String password; // 사용자 패스워드
}
