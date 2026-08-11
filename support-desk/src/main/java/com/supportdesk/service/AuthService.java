package com.supportdesk.service;

import com.supportdesk.dto.auth.*;
import com.supportdesk.dto.common.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout();

}
