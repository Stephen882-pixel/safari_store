package com.example.api.controller;

import com.example.api.dto.AddToCartRequest;
import com.example.api.dto.CartDTO;
import com.example.api.dto.CartItemDTO;
import com.example.api.dto.UpdateCartItemRequest;
import com.example.api.service.CartService;
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
    public ResponseEntity<CartDTO> getUserCart(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        CartDTO cart = cartService.getUserCart(userId);
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping("/items")
    public ResponseEntity<CartItemDTO> addItemToCart(@Valid @RequestBody AddToCartRequest request,
                                                    Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        CartItemDTO cartItem = cartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable Long itemId,
                                                     @Valid @RequestBody UpdateCartItemRequest request,
                                                     Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        CartItemDTO cartItem = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(cartItem);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId,
                                                  Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // This method should extract the user ID from the JWT token
        // The implementation depends on your authentication setup
        // For example, if you store the user ID in the token subject:
        // return Long.parseLong(authentication.getName());
        
        // Or if you have a custom UserPrincipal:
        // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        // return userPrincipal.getId();
        
        // Placeholder implementation - replace with your actual logic
        return 1L; // Replace this with actual user ID extraction
    }
}