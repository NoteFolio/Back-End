package com.example.notefolio.user.service;

import com.example.notefolio.security.jwt.GeneratedToken;
import com.example.notefolio.security.jwt.JwtUtil;
import com.example.notefolio.user.dto.request.RequestSigninDto;
import com.example.notefolio.user.dto.request.RequestSignupDto;
import com.example.notefolio.user.entity.Role;
import com.example.notefolio.user.entity.SignupType;
import com.example.notefolio.user.entity.State;
import com.example.notefolio.user.entity.User;
import com.example.notefolio.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    //findByEmail 성공 테스트
    @Test
    void testFindByEmail_Success() {
        // given
        String email = "test@example.com";
        User user = User.builder().email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        User result = userService.findByEmail(email);

        // then
        assertEquals(email, result.getEmail());
    }

    //findByEmail 실패 테스트 - 메일이 존재하지 않을 시
    @Test
    void testFindByEmail_NotFound() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByEmail(email));
    }

    // 로그인 성공 테스트
    @Test
    void testSignin_Success() {
        //given
        String email = "user@test.com";
        String rawPassword = "Test@1234";
        String encodedPassword = "endcodedTest@1234";
        String role = "NORMAL";

        RequestSigninDto dto = new RequestSigninDto(email, rawPassword);

        User user = User.builder()
                .id(1L)
                .email(email)
                .password(encodedPassword)
                .name("testname")
                .signup_type(SignupType.EMAIL)
                .state(State.ACTIVATION)
                .role(Role.valueOf(role))
                .build();

        GeneratedToken token = new GeneratedToken("access", "refresh");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(email, role)).thenReturn(token);

        //when
        GeneratedToken result = userService.signin(dto);

        //then
        assertNotNull(result);
        assertEquals("access", result.getAccessToken());
    }


    // signin password 실패 테스트
    @Test
    void testSignin_InvalidPassword() {
        //given
        String email = "test@naver.com";
        String rawPassword = "Pass@12345";
        String encodedPassword = "encodedPass@12345";

        RequestSigninDto dto = new RequestSigninDto(email, rawPassword);
        User user = User.builder().email(email).password(encodedPassword).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        //when
        //then
        assertNull(userService.signin(dto));
    }

    // siginup 성공 테스트
    @Test
    void testSignup_Success() {
        //given
        String email = "test@naver.com";
        String rawPassword = "Test@12345";
        String encodedPassword = "encodedTest@12345";

        RequestSignupDto dto = mock(RequestSignupDto.class);
        User user = User.builder().email(email).build();

        when(dto.getEmail()).thenReturn(email);
        when(dto.getPassword()).thenReturn(rawPassword);
        when(dto.getSignup_type()).thenReturn(SignupType.EMAIL);
        when(dto.isValid()).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(dto.toEntity(encodedPassword)).thenReturn(user);

        //when
        boolean result = userService.signup(dto);

        //then
        assertTrue(result);
        verify(userRepository).save(user);
    }

    //signup 실패 테스트- 이미 존재하는 메일
    @Test
    void testSignup_EmailAlreadyExists() {
        //given
        String email = "exists@test.com";
        RequestSignupDto dto = mock(RequestSignupDto.class);

        User user = User.builder()
                .id(1L)
                .email("test@naver.com")
                .password("Test@1234")
                .name("testname")
                .signup_type(SignupType.EMAIL)
                .state(State.ACTIVATION)
                .role(Role.NORMAL)
                .build();

        when(dto.getEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        //when
        boolean result = userService.signup(dto);

        //then
        assertFalse(result);
    }

    //signup 실패 테스트 - email 가입자의 경우 패스워드 입력 유효성에 맞지 않을 시
    @Test
    void testSignup_InvalidPassword() {
        RequestSignupDto dto = new RequestSignupDto(
                SignupType.EMAIL,
                "test@naver.com",
                "123412312@",
                "test"
        );

        boolean valid = dto.isValid();

        assertFalse(valid);
    }
}
