package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.response.TokenDto;
import com.springboot.cozyheavenstay.utility.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtility jwtUtility;

    @GetMapping("/api/auth/login")
    public TokenDto login(Principal principal){

        String token = jwtUtility.generateToken(principal.getName());
        return new TokenDto(
            token,
                jwtUtility.extractExpiration(token),
                LocalDateTime.now().toString()
        );
    }
}
