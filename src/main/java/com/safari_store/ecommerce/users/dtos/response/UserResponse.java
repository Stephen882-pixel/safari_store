package com.safari_store.ecommerce.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime dateJoined;
    private UserProfile profile;
    private List<AddressResponse> userAddresses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserProfile {
        private String phoneNumber;
        private String nationalId;
        private LocalDate dateOfBirth;
        private String gender;
        private String profileImage;
    }
}
