package com.safari_store.ecommerce.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.safari_store.ecommerce.users.models.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponse {
    private Long id;
    private Address.AddressType addressType;
    private String country;
    private String county;
    private String constituency;
    private String town;
    private String estate;
    private String street;
    private String landmark;
    private String postalCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
