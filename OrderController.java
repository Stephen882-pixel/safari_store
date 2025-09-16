package com.example.api.controller;

import com.example.api.dto.CancelOrderRequest;
import com.example.api.dto.CreateOrderRequest;
import com.example.api.dto.OrderDTO;
import com.example.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                               Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDTO order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getUserOrders(Pageable pageable,
                                                       Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Page<OrderDTO> orders = orderService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId,
                                                Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDTO order = orderService.getUserOrderById(userId, orderId);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId,
                                               @Valid @RequestBody CancelOrderRequest request,
                                               Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDTO order = orderService.cancelOrder(userId, orderId, request);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/{orderId}/track")
    public ResponseEntity<OrderDTO> trackOrder(@PathVariable Long orderId,
                                              Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDTO order = orderService.trackOrder(userId, orderId);
        return ResponseEntity.ok(order);
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