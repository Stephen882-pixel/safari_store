package com.safari_store.ecommerce.users.dtos.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.safari_store.ecommerce.users.models.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressRequest {

    @NotNull(message = "Address type is required")
    private Address.AddressType addressType;

    @NotBlank(message = "County is required")
    @Size(max = 100, message = "County name must not exceed 100 characters")
    private String county;

    @NotBlank(message = "Constituency is required")
    @Size(max = 100, message = "Constituency name must not exceed 100 characters")
    private String constituency;

    @NotBlank(message = "Town is required")
    @Size(max = 100, message = "Town name must not exceed 100 characters")
    private String town;

    @Size(max = 100, message = "Estate name must not exceed 100 characters")
    private String estate;

    @Size(max = 100, message = "Street name must not exceed 100 characters")
    private String street;

    @Size(max = 255, message = "Landmark must not exceed 255 characters")
    private String landmark;

    @Size(max = 55, message = "Postal code must not exceed 55 characters")
    private String postalCode;

    private String country = "Kenya"; // Default to Kenya
}
