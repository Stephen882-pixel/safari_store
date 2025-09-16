package com.safari_store.ecommerce.order.DTOS;

import com.safari_store.ecommerce.products.DTOS.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private ProductDTO product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;


    private String productName;
    private String productDescription;
    private String productImageUrl;

    private LocalDateTime createdAt;
}
