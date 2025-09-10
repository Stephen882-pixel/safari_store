package com.safari_store.ecommerce.users.controllers;

import com.safari_store.ecommerce.users.dtos.response.AddressResponse;
import com.safari_store.ecommerce.users.dtos.response.ApiResponse;
import com.safari_store.ecommerce.users.service.AddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/addresses")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "Bearer Authorization")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Appendable<List<AddressResponse>>> getUserAddresses(){
        log.info("Fetching user addresses");
        List<AddressResponse> addresses = addressService.getUserAddresses();

        return ResponseEntity.ok(ApiResponse.success(
           "Addresses retrieved succesfully",
                addresses
        ));
    }

}
