package com.safari_store.ecommerce.users.controllers;


import com.safari_store.ecommerce.users.dtos.request.UserProfileUpdateRequest;
import com.safari_store.ecommerce.users.dtos.response.ApiResponse;
import com.safari_store.ecommerce.users.dtos.response.PaginatedUsersResponse;
import com.safari_store.ecommerce.users.dtos.response.UserResponse;
import com.safari_store.ecommerce.users.service.AddressService;
import com.safari_store.ecommerce.users.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(){
        log.info("Getting user profile");
        UserResponse userResponse = userService.getUserProfile();

        return ResponseEntity.ok(ApiResponse.success(
                "User data retrieved successfully",
                userResponse
        ));
    }

    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request
            ){
        log.info("Updating user profile");
        try{
            UserResponse updatedUser = userService.updateUserProfile(request);
            return ResponseEntity.ok(ApiResponse.success(
               "User profile update successfully",
               updatedUser
            ));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(ApiResponse.error(
               e.getMessage(),
               null
            ));
        }
    }

    public ResponseEntity<ApiResponse<PaginatedUsersResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        log.info("Getting all users - page: {}, size: {}", page, size);
        try{
            PaginatedUsersResponse users = userService.getAllUsers(page, size);
            return ResponseEntity.ok(ApiResponse.success(
               "All users retrieved successfully",
               users
            ));
        } catch (Exception e){
            log.error("Error fetching users: ");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(
               "Error fetching users: " + e.getMessage(),
                    null
            ));
        }
    }

}
