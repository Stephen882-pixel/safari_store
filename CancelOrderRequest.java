package com.example.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderRequest {
    
    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    private String reason;
}