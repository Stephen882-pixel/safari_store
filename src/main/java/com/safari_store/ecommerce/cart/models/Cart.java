package com.safari_store.ecommerce.cart.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",nullable = false,unique = true)
    private Long userId;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "total_amount",precision = 10,scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "total_items")
    private Integer totalItems = 0;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,unique = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void calculateTotals() {
        this.totalAmount = items.stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        this.totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public void addItem(CartItem item){
        items.add(item);
        items.setCart(this);
        calculateTotals();
    }

    public void removeItem(CartItem item){
        items.remove(item);
        calculateTotals();
    }
}
