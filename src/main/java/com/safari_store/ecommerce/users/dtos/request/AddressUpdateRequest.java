package com.safari_store.ecommerce.users.dtos.request;

import com.safari_store.ecommerce.users.models.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressUpdateRequest {
    private Long id;
    private Address.AddressType addressType;
    private String county;
    private String constituency;
    private String town;
    private String estate;
    private String street;
    private String landmark;
    private String postalCode;
    private String country = "Kenya";
}
