package com.safari_store.ecommerce.order.Controller;


import com.safari_store.ecommerce.order.DTOS.OrderDTO;
import com.safari_store.ecommerce.order.DTOS.Request.CancelOrderRequest;
import com.safari_store.ecommerce.order.DTOS.Request.CreateOrderRequest;
import com.safari_store.ecommerce.order.service.OrderService;
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
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request, Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDTO  order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getUsersOrder(Pageable pageable,
                                                        Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        Page<OrderDTO> orders = orderService.getUsersOrders(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId, Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDTO order = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId, @Valid @RequestBody CancelOrderRequest request, Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDTO order = orderService.cancelOrder(userId, orderId, request);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/track")
    public ResponseEntity<OrderDTO> trackOrder(@PathVariable Long orderId, Authentication authentication){
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDTO order = orderService.trackOrder(userId, orderId);
        return ResponseEntity.ok(order);
    }

    private Long getUserIdFromAuthentication(Authentication authentication){
        return 1L;
    }
}
