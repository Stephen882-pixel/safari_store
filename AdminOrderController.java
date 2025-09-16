package com.example.api.controller;

import com.example.api.dto.OrderDTO;
import com.example.api.dto.UpdateOrderStatusRequest;
import com.example.api.entity.OrderStatus;
import com.example.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(Pageable pageable,
                                                      @RequestParam(required = false) OrderStatus status) {
        Page<OrderDTO> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatus(status, pageable);
        } else {
            orders = orderService.getAllOrders(pageable);
        }
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId,
                                                     @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderDTO order = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(order);
    }
}