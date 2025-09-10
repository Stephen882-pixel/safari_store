package com.safari_store.ecommerce.users.controllers;

import com.safari_store.ecommerce.users.dtos.request.AddressRequest;
import com.safari_store.ecommerce.users.dtos.response.AddressResponse;
import com.safari_store.ecommerce.users.dtos.response.ApiResponse;
import com.safari_store.ecommerce.users.service.AddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddresses(){
        log.info("Fetching user addresses");
        List<AddressResponse> addresses = addressService.getUserAddresses();

        return ResponseEntity.ok(ApiResponse.success(
           "Addresses retrieved succesfully",
                addresses
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request
            ){
        log.info("Creating address request");
        try {
            AddressResponse addrress = addressService.createAddress(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
               "Adderss create successfully",
               addrress
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                       e.getMessage(),
                       null
                    ));
        }
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddress(@PathVariable Long id){
        log.info("Getting address with id {}", id);
        try {
            AddressResponse addrress = addressService.getAddress(id);
            return ResponseEntity.ok(ApiResponse.success(
               "Address retrieved successfully",
                    addrress
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
