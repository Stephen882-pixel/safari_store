package com.safari_store.ecommerce.users.controllers;


import com.safari_store.ecommerce.users.dtos.response.ApiResponse;
import com.safari_store.ecommerce.users.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/promote/{username}")
    public ResponseEntity<ApiResponse<String>> promoteToUser(@PathVariable String username){
        adminService.promoteToAdmin(username);
        return ResponseEntity.ok(ApiResponse.success(
           "User promoted to admin successfully",
           "User " + username + " is now an ADMIN"
        ));
    }

    @PostMapping("/demote/{username}")
    public ResponseEntity<ApiResponse<String>> demoteToUser(@PathVariable String username){
        adminService.demoteToUser(username);
        return ResponseEntity.ok(ApiResponse.success(
           "Admin demoted to user successfully",
                "User " + username + " is now a USER"
        ));
    }
}
