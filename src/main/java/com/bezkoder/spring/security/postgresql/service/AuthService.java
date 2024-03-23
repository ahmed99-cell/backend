package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.config.ResetPasswordRequest;
import com.bezkoder.spring.security.postgresql.payload.request.LoginRequest;
import com.bezkoder.spring.security.postgresql.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
    ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest);
    ResponseEntity<?> registerUser(SignupRequest signUpRequest);
}
