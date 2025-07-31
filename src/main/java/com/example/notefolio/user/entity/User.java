package com.example.notefolio.user.entity;

import com.example.notefolio.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Table(name="`user`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@SuperBuilder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Enumerated(EnumType.STRING)
    private SignupType signup_type; // 회원가입 유형( EMAIL, KAKAO, NAVER, GOOGLE 등)

    private String email; // 메일 주소

    private String password; // 암호화 된 패스워드

    private String name; // 사용자 명

    private String profile_image; // 이미지 경로

    private String introduction; // 자기 소개

    private String github_url; // GitHub 주소

    @Enumerated(EnumType.STRING)
    private State state; // 사용자 상태( REGISTRATION 등록 상태, ACTIVATION 활성화 상태, LOCK 잠김 상태)

    @Enumerated(EnumType.STRING)
    private Role role; // 관리자, 사용자 분류(ADMIN, NORMAL)

    private String activation_id; // 활성화 키 저장

    private Date activation_at; // 활성화 일자

}
