package com.safari_store.ecommerce.users.controllers;


import com.safari_store.ecommerce.users.dtos.request.*;
//import com.safari_store.ecommerce.users.dtos.request.PasswordResetRequest;
import com.safari_store.ecommerce.users.dtos.response.ApiResponse;
import com.safari_store.ecommerce.users.dtos.response.AuthResponse;
import com.safari_store.ecommerce.users.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;


    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request){
        log.info("Registration requet for username: {}",request.getUsername());
        ApiResponse<?> response = authService.register(request);

        HttpStatus status = "success".equals(response.getStatus()) ?
                HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }


    public ResponseEntity<ApiResponse<AuthResponse.AuthData>> login(@Valid @RequestBody LoginRequest request){
        log.info("Login request from email: {}",request.getEmail());
        ApiResponse<AuthResponse.AuthData> response = authService.login(request);

        HttpStatus status = "success".equals(response.getStatus()) ?
                HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    public ResponseEntity<ApiResponse<?>> verifyOTP(@Valid @RequestBody VerifyOTPRequest request){
        log.info("OTP Verification request for email: {}",request.getEmail());
        ApiResponse<?> response = authService.verifyOTP(request);

        HttpStatus status = "success".equals(response.getStatus()) ?
                HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    public ResponseEntity<ApiResponse<?>>  logout(@Valid @RequestBody LogoutRequest request){
        log.info("Log out request recieved");
        ApiResponse<?> response = authService.logout(request);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<AuthResponse.AuthData>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request
            ){
        log.info("Token refresh requst received");
        ApiResponse<AuthResponse.AuthData> response = authService.refreshToken(request);

        HttpStatus status = "success".equals(response.getStatus()) ?
                HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }
    
    public ResponseEntity<ApiResponse<?>> requestPasswordReset(@Valid @RequestBody PasswordRequestReset  request){
        log.info("Password reset request for email : {}",request.getEmail());
        ApiResponse<?> response = authService.requestPasswordReset(request);

        return ResponseEntity.ok(response);
    }
    
    public ResponseEntity<ApiResponse<?>> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        log.info("Password reset confirmation for email: {}",request.getEmail());
        ApiResponse<?> response = authService.resetPassword(request);
        HttpStatus status = "success".equals(response.getStatus()) ?
                HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status().body(response);
    }


}
