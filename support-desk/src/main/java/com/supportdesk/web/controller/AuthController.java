package com.supportdesk.web.controller;

import com.supportdesk.infrastructure.persistence.entity.CommentEntity;
import com.supportdesk.infrastructure.persistence.entity.UserEntity;
import com.supportdesk.infrastructure.persistence.repository.UserJpaRepository;
import com.supportdesk.infrastructure.security.CustomUserDetails;
import com.supportdesk.infrastructure.security.JwtTokenProvider;
import com.supportdesk.web.dto.request.LoginRequest;
import com.supportdesk.web.dto.request.RegisterRequest;
import com.supportdesk.web.dto.response.ApiResponse;
import com.supportdesk.web.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Registration and login — issues JWTs")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        if (userJpaRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(409).body(ApiResponse.success(null, "Username already taken"));
        }
        UserEntity user = new UserEntity(
                UUID.randomUUID(),
                request.username(),
                passwordEncoder.encode(request.password()),
                CommentEntity.RoleJpa.valueOf(request.role())
        );
        userJpaRepository.save(user);
        log.info("Registered new user {} with role {}", request.username(), request.role());
        return ResponseEntity.status(201).body(ApiResponse.success(null, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String token = tokenProvider.generateToken(principal);
        log.info("User {} logged in", request.username());
        return ResponseEntity.ok(ApiResponse.success(AuthResponse.bearer(token)));
    }
}