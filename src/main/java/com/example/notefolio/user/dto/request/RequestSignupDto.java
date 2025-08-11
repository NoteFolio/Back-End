package com.example.notefolio.user.dto.request;

import com.example.notefolio.user.entity.Role;
import com.example.notefolio.user.entity.SignupType;
import com.example.notefolio.user.entity.State;
import com.example.notefolio.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestSignupDto {

    @NotNull
    private SignupType signup_type; // 회원가입 유형( EMAIL, KAKAO, NAVER, GOOGLE 등)

    @Email
    @NotBlank
    private String email; // 메일 주소

    private String password; // 암호화 된 패스워드

    private String name; // 사용자 명

    // 유효성 검사
    // 메일 가입 사용자의 경우 비밀번호 유효성 검사
    public boolean isValid(){
        if (signup_type.equals(SignupType.EMAIL)){
            return isPasswordValid();
        }
        return true;
    }

    public User toEntity(String passwordEncoded) {
        return User.builder()
                .signup_type(this.signup_type)
                .email(this.email)
                .password(passwordEncoded)
                .name(this.name)
                .role(Role.NORMAL)
                .state(State.REGISTRATION)
                .build();
    }

    // 패스워드 유효성 검사
    // 대소문자, 특수문자, 숫자 조합 8자리 이상
    private boolean isPasswordValid(){
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()\\-_=+{};:,<.>].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
}


