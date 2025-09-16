package com.example.api.dto;

import com.example.api.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private List<OrderItemDTO> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Integer totalItems;
    
    // Shipping Information
    private String shippingName;
    private String shippingEmail;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;
    
    // Order tracking
    private String trackingNumber;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    
    // Payment information
    private String paymentMethod;
    private String paymentStatus;
    private String notes;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}