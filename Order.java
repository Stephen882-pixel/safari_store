package com.example.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;
    
    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(name = "total_items", nullable = false)
    private Integer totalItems = 0;
    
    // Shipping Information
    @Column(name = "shipping_name", nullable = false)
    private String shippingName;
    
    @Column(name = "shipping_email", nullable = false)
    private String shippingEmail;
    
    @Column(name = "shipping_phone")
    private String shippingPhone;
    
    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;
    
    @Column(name = "shipping_city", nullable = false)
    private String shippingCity;
    
    @Column(name = "shipping_state")
    private String shippingState;
    
    @Column(name = "shipping_postal_code")
    private String shippingPostalCode;
    
    @Column(name = "shipping_country", nullable = false)
    private String shippingCountry;
    
    // Order tracking
    @Column(name = "tracking_number")
    private String trackingNumber;
    
    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    // Payment information
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "payment_status")
    private String paymentStatus = "PENDING";
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void generateOrderNumber() {
        if (orderNumber == null) {
            orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
    
    // Helper methods
    public void calculateTotals() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalItems = items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotals();
    }
    
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
    
    public void cancel(String reason) {
        if (canBeCancelled()) {
            this.status = OrderStatus.CANCELLED;
            this.cancelledAt = LocalDateTime.now();
            this.cancellationReason = reason;
        } else {
            throw new IllegalStateException("Order cannot be cancelled in current status: " + status);
        }
    }
}