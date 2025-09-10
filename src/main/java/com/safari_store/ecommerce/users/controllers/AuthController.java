package com.safari_store.ecommerce.users.controllers;


import com.safari_store.ecommerce.users.dtos.request.RegisterRequest;
import com.safari_store.ecommerce.users.dtos.response.ApiResponse;
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


}
