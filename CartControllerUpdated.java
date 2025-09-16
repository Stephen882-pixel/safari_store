package com.example.api.controller;

import com.example.api.dto.AddToCartRequest;
import com.example.api.dto.CartDTO;
import com.example.api.dto.CartItemDTO;
import com.example.api.dto.UpdateCartItemRequest;
import com.example.api.security.UserPrincipal; // Adjust import based on your package
import com.example.api.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping
    public ResponseEntity<CartDTO> getUserCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartDTO cart = cartService.getUserCart(userPrincipal.getId());
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping("/items")
    public ResponseEntity<CartItemDTO> addItemToCart(@Valid @RequestBody AddToCartRequest request,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartItemDTO cartItem = cartService.addItemToCart(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable Long itemId,
                                                     @Valid @RequestBody UpdateCartItemRequest request,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartItemDTO cartItem = cartService.updateCartItem(userPrincipal.getId(), itemId, request);
        return ResponseEntity.ok(cartItem);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId,
                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        cartService.removeItemFromCart(userPrincipal.getId(), itemId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        cartService.clearCart(userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}