package org.example.tdlsb.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tdlsb.dto.CreateUserRequest;
import org.example.tdlsb.dto.LoginRequest;
import org.example.tdlsb.dto.LoginResponse;
import org.example.tdlsb.dto.UserResponse;
import org.example.tdlsb.security.JwtTokenProvider;
import org.example.tdlsb.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /api/auth/register - Registering new user: {}", request.getUsername());

        UserResponse created = userService.createUser(request);

        return ResponseEntity.ok(created);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Login attempt for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерируем JWT токен
        String jwt = tokenProvider.generateToken(authentication);

        LoginResponse response = LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .username(request.getUsername())
                .build();

        log.info("User {} logged in successfully, token generated", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Для JWT просто очищаем контекст
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }
}