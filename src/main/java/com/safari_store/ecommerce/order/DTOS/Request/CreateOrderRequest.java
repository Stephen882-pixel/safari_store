package com.safari_store.ecommerce.order.DTOS.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @NotBlank(message = "Shipping name is required")
    @Size(max = 100, message = "Shipping name cannot exceed 100 characters")
    private String shippingName;

    @NotBlank(message = "Shipping email is required")
    @Email(message = "Please provide a valid email address")
    private String shippingEmail;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String shippingPhone;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
    private String shippingAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String shippingCity;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String shippingState;

    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String shippingPostalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String shippingCountry;


    @Size(max = 50, message = "Payment method cannot exceed 50 characters")
    private String paymentMethod = "CASH_ON_DELIVERY";

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
