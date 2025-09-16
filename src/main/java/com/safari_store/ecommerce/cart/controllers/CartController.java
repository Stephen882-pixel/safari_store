package com.safari_store.ecommerce.cart.controllers;

import com.safari_store.ecommerce.cart.DTO.AddToCartRequest;
import com.safari_store.ecommerce.cart.DTO.CartDTO;
import com.safari_store.ecommerce.cart.DTO.CartItemDTO;
import com.safari_store.ecommerce.cart.DTO.UpdateCartItemRequest;
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

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request,Authentication authentication){
        Long useId = getUserIdFromAuthentication(authentication);
        CartItemDTO cartItem = cartService.updateCartItem(useId,itemId,request);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId,Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        cartService.removeItemFromCart(userId,itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

}
