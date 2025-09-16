package com.example.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private ProductDTO product;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}