package com.supportdesk.service.impl;

import com.supportdesk.dto.auth.*;
import com.supportdesk.dto.common.UserResponse;
import com.supportdesk.entity.RefreshTokenEntity;
import com.supportdesk.entity.UserEntity;
import com.supportdesk.enums.Role;
import com.supportdesk.enums.UserStatus;
import com.supportdesk.exception.*;
import com.supportdesk.exception.UserNotFoundException;
import com.supportdesk.mapper.UserMapper;
import com.supportdesk.repository.UserRepository;
import com.supportdesk.security.custom.CustomUserDetails;
import com.supportdesk.security.jwt.JwtProperties;
import com.supportdesk.security.jwt.JwtService;
import com.supportdesk.security.utils.SecurityUtils;
import com.supportdesk.service.AuthService;
import com.supportdesk.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;
    private final JwtProperties  jwtProperties;


    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailExistsException();
        }

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(user);

        return userMapper.toResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () ->  new UserNotFoundException("User not found with email: " + request.getEmail())
                );

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (AuthenticationException ex) {

            throw ex;
        }

        String accessToken =
                jwtService.generateAccessToken(
                        new CustomUserDetails(user)
                );

        RefreshTokenEntity refreshToken =
                refreshTokenService.create(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtProperties.getAccessExpiration())
                .build();
    }

    @Override
    public AuthResponse refresh(
            RefreshTokenRequest request
    ) {

        RefreshTokenEntity refreshToken =
                refreshTokenService.verify(
                        request.refreshToken()
                );

        UserEntity user = refreshToken.getUser();

        RefreshTokenEntity newRefresh =
                refreshTokenService.rotate(refreshToken);

        String accessToken =
                jwtService.generateAccessToken(
                        new CustomUserDetails(user)
                );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefresh.getToken())
                .expiresIn(jwtProperties.getAccessExpiration())
                .build();

    }

    @Override
    public void logout() {

        UserEntity user = userRepository.findById(
                SecurityUtils.getCurrentUserId()
        ).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + SecurityUtils.getCurrentUserId())
        );

        refreshTokenService.revokeAll(user);

    }
}
