package com.safari_store.ecommerce.users.config;

import com.safari_store.ecommerce.users.Enum.UserRole;
import com.safari_store.ecommerce.users.models.User;
import com.safari_store.ecommerce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createInitialAdmin();
    }

    private void createInitialAdmin() {
        if(userRepository.findByRole(UserRole.ADMIN).isEmpty()){
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("ondeyostephen0@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);
            log.warn("Initial admin user created - Username: admin, Password: admin123");
            log.warn("PLEASE CHANGE THE DEFAULT ADMIN PASSWORD IMMEDIATELY!");
        }
    }
}
