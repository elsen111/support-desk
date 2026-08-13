package com.supportdesk.web.controller;

import com.supportdesk.infrastructure.persistence.entity.CommentEntity;
import com.supportdesk.infrastructure.persistence.entity.UserEntity;
import com.supportdesk.infrastructure.persistence.repository.UserJpaRepository;
import com.supportdesk.infrastructure.security.CustomUserDetails;
import com.supportdesk.infrastructure.security.JwtTokenProvider;
import com.supportdesk.infrastructure.security.RefreshTokenResult;
import com.supportdesk.infrastructure.security.RefreshTokenService;
import com.supportdesk.web.dto.request.LoginRequest;
import com.supportdesk.web.dto.request.RefreshTokenRequest;
import com.supportdesk.web.dto.request.RegisterRequest;
import com.supportdesk.web.dto.response.ApiResponse;
import com.supportdesk.web.dto.response.AuthResponse;
import com.supportdesk.web.dto.response.UserResponse;
import com.supportdesk.web.exception.InvalidRefreshTokenException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            UserJpaRepository userJpaRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            RefreshTokenService refreshTokenService
    ) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        if (userJpaRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(409).body(ApiResponse.success(null, "Username already taken"));
        }
        UserEntity user = new UserEntity(
                UUID.randomUUID(),
                request.username(),
                passwordEncoder.encode(request.password()),
                CommentEntity.RoleJpa.valueOf(request.role())
        );
        UserEntity newUser = userJpaRepository.save(user);
        log.info("Registered new user {} with role {}", request.username(), request.role());
        return ResponseEntity.status(201).body(ApiResponse.success(UserResponse.from(newUser), "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = tokenProvider.generateToken(principal);
        String refreshToken = refreshTokenService.issueToken(principal.getUserId());
        UserResponse userResponse = new UserResponse(principal.getUserId(), principal.getUsername(), principal.getRole());

        log.info("User {} logged in", request.username());

        return ResponseEntity.status(200).body(ApiResponse.success(
                AuthResponse.of(accessToken, refreshToken, userResponse), "User logged in successfully")
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResult rotated = refreshTokenService.validateAndRotate(request.refreshToken());

        UserEntity user = userJpaRepository.findById(rotated.userId())
                .orElseThrow(() -> new InvalidRefreshTokenException("User associated with this token no longer exists"));

        CustomUserDetails principal = new CustomUserDetails(user);
        String newAccessToken = tokenProvider.generateToken(principal);
        UserResponse userResponse = UserResponse.from(user);

        log.info("Refreshed access token for user {}", user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                AuthResponse.of(newAccessToken, rotated.rawRefreshToken(), userResponse), "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revoke(request.refreshToken());
        log.info("Refresh token revoked (logout)");
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

}