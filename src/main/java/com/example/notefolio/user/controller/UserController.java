package com.example.notefolio.user.controller;

import com.example.notefolio.security.jwt.GeneratedToken;
import com.example.notefolio.user.dto.request.RequestSigninDto;
import com.example.notefolio.user.dto.request.RequestSignupDto;
import com.example.notefolio.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody RequestSigninDto rsd) {
        GeneratedToken generatedToken = userService.signin(rsd);

        return ResponseEntity.ok(generatedToken);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RequestSignupDto rsd) {
        boolean b = userService.signup(rsd);
        if (!b){
            return ResponseEntity.badRequest().body("fail");
        }
        return ResponseEntity.ok("success");
    }
}
