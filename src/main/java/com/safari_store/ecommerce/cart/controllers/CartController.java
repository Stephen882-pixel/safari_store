package com.safari_store.ecommerce.cart.controllers;

import com.safari_store.ecommerce.cart.DTO.AddToCartRequest;
import com.safari_store.ecommerce.cart.DTO.CartDTO;
import com.safari_store.ecommerce.cart.DTO.CartItemDTO;
import com.safari_store.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/items")
    public ResponseEntity<CartItemDTO> addItemToCart(@Valid @RequestBody AddToCartRequest request, Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        CartItemDTO cartItem = cartService.addItemToCart(userId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

}
