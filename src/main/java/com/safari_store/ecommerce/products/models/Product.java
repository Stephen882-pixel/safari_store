package com.safari_store.ecommerce.products.models;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long field;

    @NotBlank(message = "product name is required")
    @Size(min = 2,max = 200, message = "product name must be between 2 and 200 characters")
    @Column(nullable = false)
    private String name;

    @Size(max=1000,message = "Description cannot exceed more than 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0",inclusive = false,message = "price must be greater than 0")
    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price;

    @Min(value = 0,message = "Stock quantity cannot be negative")
    @Column(name = "stock_quantity",nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "product_images",joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> additionalImages = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean featured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @ElementCollection
    @CollectionTable(name = "product_tags",joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
