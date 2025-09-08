package com.safari_store.ecommerce.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSummaryResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profileImage;
}
