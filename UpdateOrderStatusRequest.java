package com.example.api.dto;

import com.example.api.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Order status is required")
    private OrderStatus status;
    
    @Size(max = 100, message = "Tracking number cannot exceed 100 characters")
    private String trackingNumber;
    
    private LocalDateTime estimatedDelivery;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}