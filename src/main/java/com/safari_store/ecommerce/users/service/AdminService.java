package com.safari_store.ecommerce.users.service;

import com.safari_store.ecommerce.users.Enum.UserRole;
import com.safari_store.ecommerce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.safari_store.ecommerce.users.models.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public void promoteToAdmin(String username){
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if(user.getRole() == UserRole.ADMIN){
            log.info("User {} is already an admin", username);
            return;
        }

        user.setRole(UserRole.ADMIN);
        userRepository.save(user);

        log.warn("User {} promoted to admin", username);
    }

    @Transactional
    public void demoteToUser(String username){
        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (user.getRole() != UserRole.ADMIN){
            log.info("User {} is not an admin", username);
            return;
        }
        user.setRole(UserRole.USER);
        userRepository.save(user);

        log.warn("User {} demoted from ADMIN to User", username);
    }
}
