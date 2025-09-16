package com.example.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // Store product details at time of order (for historical purposes)
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "product_description", length = 1000)
    private String productDescription;
    
    @Column(name = "product_image_url")
    private String productImageUrl;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Helper method to calculate subtotal
    public void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    @PrePersist
    public void prePersist() {
        if (product != null) {
            this.unitPrice = product.getPrice();
            this.productName = product.getName();
            this.productDescription = product.getDescription();
            this.productImageUrl = product.getImageUrl();
        }
        calculateSubtotal();
    }
}