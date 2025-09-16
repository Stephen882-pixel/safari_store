package com.safari_store.ecommerce.order.Controller;


import com.safari_store.ecommerce.order.DTOS.OrderDTO;
import com.safari_store.ecommerce.order.OrderStatus;
import com.safari_store.ecommerce.order.models.Order;
import com.safari_store.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(Pageable pageable, @RequestParam(required = false)OrderStatus status){
        Page<OrderDTO> orders;
        if(status != null){
            orders = orderService.getOrderByStatus(status, pageable);
        } else {
            orders = orderService.getAllOrders(pageable);
        }
        return ResponseEntity.ok(orders);
    }
}
