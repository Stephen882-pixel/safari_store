package com.example.api.controller;

import com.example.api.dto.CancelOrderRequest;
import com.example.api.dto.CreateOrderRequest;
import com.example.api.dto.OrderDTO;
import com.example.api.security.UserPrincipal; // Adjust import based on your package
import com.example.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderDTO order = orderService.createOrder(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getUserOrders(Pageable pageable,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Page<OrderDTO> orders = orderService.getUserOrders(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderDTO order = orderService.getUserOrderById(userPrincipal.getId(), orderId);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId,
                                               @Valid @RequestBody CancelOrderRequest request,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderDTO order = orderService.cancelOrder(userPrincipal.getId(), orderId, request);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/{orderId}/track")
    public ResponseEntity<OrderDTO> trackOrder(@PathVariable Long orderId,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderDTO order = orderService.trackOrder(userPrincipal.getId(), orderId);
        return ResponseEntity.ok(order);
    }
}