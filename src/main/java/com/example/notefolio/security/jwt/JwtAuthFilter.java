package com.example.notefolio.security.jwt;

import com.example.notefolio.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("Request URI : {}", requestURI);

        if(requestURI.equals("/") || requestURI.equals("/signin") || requestURI.equals("/signup") // 해당 요청 url 경로는 토큰 검사 진행 x
        ){
            filterChain.doFilter(request, response);
            return;
        }

        final String autorization = request.getHeader(HttpHeaders.AUTHORIZATION); // 헤더에서 토큰 가져온다.
        log.info("authorization : {}", autorization);

        String token = autorization.split(" ")[1]; // Bearer 제거

        if (!StringUtils.hasText(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is required");
            return;
        }

        // AccessToken을 검증하고, 만료되었을경우 예외를 발생시킨다.
        if(!jwtUtil.verifyToken(token)) {
            throw new JwtException("Access Token 만료");
        }

    }
}
