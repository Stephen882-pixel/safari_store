package com.safari_store.ecommerce.users.dtos.request;

import com.safari_store.ecommerce.users.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateRequest {
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(max = 25, message = "Phone number must not exceed 25 characters")
    private String phoneNumber;

    @Size(max = 20, message = "National ID must not exceed 20 characters")
    private String nationalId;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private User.Gender gender;

    private List<AddressUpdateRequest> addresses;
}
