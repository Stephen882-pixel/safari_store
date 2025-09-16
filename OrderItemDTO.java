package com.example.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private ProductDTO product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    
    // Historical product data
    private String productName;
    private String productDescription;
    private String productImageUrl;
    
    private LocalDateTime createdAt;
}