package com.safari_store.ecommerce.cart.controllers;

import com.safari_store.ecommerce.cart.DTO.CartDTO;
import com.safari_store.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CartController {
    private final CartService cartService;
    
    @GetMapping
    public ResponseEntity<CartDTO> getUserCart(Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        CartDTO cart = cartService.getUserCart(userId);
        return ResponseEntity.ok(cart);
    }


}
