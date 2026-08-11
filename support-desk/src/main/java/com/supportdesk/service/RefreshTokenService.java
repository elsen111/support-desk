package com.supportdesk.service;

import com.supportdesk.dto.auth.AuthResponse;
import com.supportdesk.entity.RefreshTokenEntity;
import com.supportdesk.entity.UserEntity;

public interface RefreshTokenService {

    RefreshTokenEntity create(UserEntity user);

    RefreshTokenEntity verify(String token);

    RefreshTokenEntity rotate(RefreshTokenEntity token);

    void revoke(RefreshTokenEntity token);

    AuthResponse refresh(String refreshToken);

    void revokeAll(UserEntity user);

}