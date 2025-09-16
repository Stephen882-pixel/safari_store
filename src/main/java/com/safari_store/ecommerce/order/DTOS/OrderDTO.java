package com.safari_store.ecommerce.order.DTOS;

import com.safari_store.ecommerce.order.OrderStatus;
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

    private String shippingName;
    private String shippingEmail;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;

    private String trackingNumber;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;

    private String paymentMethod;
    private String paymentStatus;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
