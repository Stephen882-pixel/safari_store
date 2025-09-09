package com.safari_store.ecommerce.users.service;

import com.safari_store.ecommerce.users.User;
import com.safari_store.ecommerce.users.dtos.request.UserProfileUpdateRequest;
import com.safari_store.ecommerce.users.dtos.response.UserResponse;
import com.safari_store.ecommerce.users.repository.AddressRepository;
import com.safari_store.ecommerce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() ->  new RuntimeException("Current user is not found: " + username));
    }

    @Transactional(readOnly = true)
    public UserResponse updateUserProfile(UserProfileUpdateRequest request){
        User user = getCurrentUser();

        if (request.getFirstName() != null){
            request.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null){
            request.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())){

        }
    }

}
