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

    public void sendPasswordResetOTP(User user, String otpCode){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject(appName + " - Password Reset Request");

            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "You have requested to reset your password for your %s account.\n\n" +
                            "Please use the following OTP to proceed with password reset:\n\n" +
                            "Reset Code: %s\n\n" +
                            "This code will expire in 10 minutes.\n\n" +
                            "If you didn't request this password reset, please ignore this email " +
                            "or contact our support team if you have concerns.\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    user.getFirstName(),
                    appName,
                    otpCode,
                    appName
            );
            message.setText(emailBody);
            mailSender.send(message);

            log.info("Password reset OTP email sent successfully to: {}", user.getEmail());
        } catch (Exception e){
            log.error("Failed to send password reset OTP email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(User user){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Welcome to " + appName + "!");

            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "Welcome to %s! Your email has been successfully verified.\n\n" +
                            "You can now log in to your account and start exploring our platform.\n\n" +
                            "If you have any questions or need support, feel free to contact us.\n\n" +
                            "Thank you for joining us!\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    user.getFirstName(),
                    appName,
                    appName
            );

            message.setText(emailBody);
            mailSender.send(message);

            log.info("Welcome email sent successfully to: {}", user.getEmail());
        } catch (Exception e){
            log.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }
    }

    public void sendPasswordChangeConfirmation(User user){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject(appName + " - Password Changed Successfully");

            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "Your password for %s has been successfully changed.\n\n" +
                            "If you didn't make this change, please contact our support team immediately.\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    user.getFirstName(),
                    appName,
                    appName
            );

            message.setText(emailBody);
            mailSender.send(message);

            log.info("Password change confirmation email sent successfully to: {}", user.getEmail());
        } catch (Exception e){
            log.error("Failed to send password change confirmation email to: {}", user.getEmail(), e);
        }
    }
}
