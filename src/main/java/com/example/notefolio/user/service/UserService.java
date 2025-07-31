package com.example.notefolio.user.service;

import com.example.notefolio.security.jwt.GeneratedToken;
import com.example.notefolio.security.jwt.JwtUtil;
import com.example.notefolio.user.dto.request.RequestSigninDto;
import com.example.notefolio.user.dto.request.RequestSignupDto;
import com.example.notefolio.user.entity.SignupType;
import com.example.notefolio.user.entity.User;
import com.example.notefolio.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email + " 계정의 Entity 찾지 못함"));
    }

    @Transactional
    public GeneratedToken signin(RequestSigninDto rsd){
        User u = findByEmail(rsd.getEmail());

        if (!passwordEncoder.matches(rsd.getPassword(), u.getPassword())) {
            return null;
        }

        return jwtUtil.generateToken(u.getEmail(), u.getRole().toString());
    }
    @Transactional
    public boolean signup(RequestSignupDto rsd){

        // 이메일 계정 존재 여부 확인
        if (userRepository.findByEmail(rsd.getEmail()).isPresent()){
            return false;
        }

        // 유효성 검사
        if (!rsd.isValid()){
            return false;
        }

        String passwordEncode = "";

        if (SignupType.EMAIL.equals(rsd.getSignup_type())){
            passwordEncode = passwordEncoder.encode(rsd.getPassword());
        }

        User u = rsd.toEntity(passwordEncode);
        userRepository.save(u);

        return true;
    }
}
