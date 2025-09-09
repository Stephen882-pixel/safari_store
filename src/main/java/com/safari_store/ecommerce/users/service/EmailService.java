package com.safari_store.ecommerce.users.service;

import com.safari_store.ecommerce.users.User;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private  final JavaMailSender mailSender;

    @Value("${spring.mail.from:ondeyostephen0@gmail.com}")
    private String fromEmail;

    private String appName;

    public void sendOTPMail(User user, String otpCode){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Welcome to " + appName  + " - Verify your email");

            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "Welcome to %s! Please use the following OTP to verify your email address:\n\n" +
                            "Verification Code: %s\n\n" +
                            "This code will expire in 10 minutes.\n\n" +
                            "If you didn't request this verification, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    user.getFirstName(),
                    appName,
                    otpCode,
                    appName
            );
            message.setText(emailBody);
            mailSender.send(message);
            log.info("Registration OTP email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send registration OTP email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

}
