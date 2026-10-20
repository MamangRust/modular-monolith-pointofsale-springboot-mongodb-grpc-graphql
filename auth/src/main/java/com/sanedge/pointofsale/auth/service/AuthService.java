package com.sanedge.pointofsale.auth.service;

import com.sanedge.pointofsale.auth.domain.requests.AuthRequest;
import com.sanedge.pointofsale.auth.domain.requests.RegisterRequest;
import com.sanedge.pointofsale.auth.domain.requests.ResetPasswordRequest;
import com.sanedge.pointofsale.auth.domain.responses.TokenResponse;
import com.sanedge.pointofsale.auth.domain.responses.UserResponse;
import com.sanedge.pointofsale.common.domain.responses.api.ApiResponse;

public interface AuthService {
    ApiResponse<TokenResponse> login(AuthRequest loginRequest);

    ApiResponse<UserResponse> register(RegisterRequest registerRequest);

    ApiResponse<TokenResponse> refreshToken(String refreshToken);

    ApiResponse<UserResponse> getCurrentUser(String username);

    ApiResponse<Void> forgotPassword(String email);

    ApiResponse<Void> resetPassword(ResetPasswordRequest resetPasswordRequest);

    ApiResponse<Void> verifyCode(String code);
}
