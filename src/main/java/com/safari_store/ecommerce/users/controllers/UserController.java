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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(){
        log.info("===CONTROLLER: Getting user profile ===");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Controller - Authentication object: {}", auth);
        log.info("Controller - Is Authenticated: {}",auth != null ? auth.isAuthenticated() : "null");
        UserResponse userResponse = userService.getUserProfile();

        return ResponseEntity.ok(ApiResponse.success(
                "User data retrieved successfully",
                userResponse
        ));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
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

    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(){
        log.info("Deleting user account");
        userService.deleteAccount();
        return ResponseEntity.noContent().build();
    }
}
