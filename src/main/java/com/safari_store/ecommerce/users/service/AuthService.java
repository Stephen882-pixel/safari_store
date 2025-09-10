package com.safari_store.ecommerce.users.service;

import com.safari_store.ecommerce.users.User;
import com.safari_store.ecommerce.users.dtos.request.*;
import com.safari_store.ecommerce.users.dtos.response.ApiResponse;
import com.safari_store.ecommerce.users.dtos.response.AuthResponse;
import com.safari_store.ecommerce.users.models.OTP;
import com.safari_store.ecommerce.users.models.PasswordResetRequest;
import com.safari_store.ecommerce.users.repository.OTPRepository;
import com.safari_store.ecommerce.users.repository.UserRepository;
import com.safari_store.ecommerce.users.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public ApiResponse<?> register(RegisterRequest request) {
        try{
            if (userRepository.existsByUsername(request.getUsername().toLowerCase())) {
                return ApiResponse.failed(
                        "Username already exists. Please choose a different username.",
                        "username already exists."
                );
            }
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())){
                return ApiResponse.failed(
                        "Phone number already exists. Please use a different phone number.",
                        "Phone number already exists."
                );
            }
            User user = new User();
            user.setUsername(request.getUsername().toLowerCase());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstname());
            user.setLastName(request.getLastname());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhoneNumber(request.getPhoneNumber());
            user.setActive(false); // User starts inactive until email verification

            User savedUser = userRepository.save(user);

            String otpCode = generateOTP();
            OTP otp = new OTP();
            otp.setUser(savedUser);
            otp.setOtpcode(otpCode);
            otpRepository.save(otp);

            emailService.sendOTPEmail(savedUser,otpCode);

            return ApiResponse.success(
                    "Account created successfully. Please check your email for OTP Verification code.",
                    null
            );
        } catch (Exception e){
            log.error("Registration error: ", e);
            return ApiResponse.failed(
                    "An unexpected error occurred: " + e.getMessage(),
                    null
            );
        }
    }
    @Transactional
    public ApiResponse<AuthResponse.AuthData> login(LoginRequest request) {
        try{
            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(request.getEmail());
            if (userOpt.isPresent()) {
                return ApiResponse.error("Invalid credentials",null)
            }
            User user = userOpt.get();

            if (!user.isActive()){
                return ApiResponse.error(
                        "Email not verified. Please verify your email.",
                        "Account not activated"
                );
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
            );

            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            AuthResponse.AuthData authData = AuthResponse.AuthData.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000)
                    .build();
            return ApiResponse.success(
                    "Login successful",
                    authData
            );
        } catch (DisabledException e){
            return ApiResponse.error(
                    "Email not verified. Please verify your email.",
                    "Account disabled"
            );
        } catch (BadCredentialsException e){
            return ApiResponse.error("Invalid credentials",null)
        } catch (AuthenticationException e){
            return ApiResponse.error(
                    "Authentication failed: " + e.getMessage(),null
            );
        } catch (Exception e){
            log.error("Login error: ", e);
            return ApiResponse.error(
                    "Login processing failed",e.getMessage()
            );
        }
    }

    @Transactional
    public ApiResponse<?> verifyOTP(VerifyOTPRequest request) {
        try {
            Optional<User>  userOpt = userRepository.findByEmailIgnoreCase(request.getEmail());
            if(userOpt.isPresent()){
                return ApiResponse.error(
                        "User not found",
                        null
                );
            }
            User user = userOpt.get();

            Optional<OTP> otpOpt = otpRepository.findLatestUnverifiedOTPByUser(user);
            if (otpOpt.isEmpty()){
                return ApiResponse.error(
                        "No OTP found for this account. Please request a new OTP",
                        null
                );
            }
            OTP otp = otpOpt.get();

            if (otp.isExpired()){
                return ApiResponse.error(
                        "OTP has expired. Please request a new one",
                        null
                );
            }
            if (!request.getOtpCode().equals(otp.getOtpcode())){
                return ApiResponse.error(
                        "Invalide OTP",
                        null
                );
            }
            otp.setVerified(true);
            otpRepository.save(otp);

            String verificationType = !user.isActive() ? "registration" : "password_reset";
            if("registration".equals(verificationType)){
                user.setActive(true);
                userRepository.save(user);

                return ApiResponse.success(
                        "Email verified succeessfully. You can now login",
                        null
                );
            } else {
                return ApiResponse.success(
                        "OTP Verified successfully. You can reset your password",
                        null
                );
            }
        } catch (Exception e){
            log.error("OTP verification error: ", e);
            return ApiResponse.error(
                    "An error occurred: " + e.getMessage(), null
            );
        }
    }

    @Transactional
    public ApiResponse<?> requestPasswordReset(PasswordRequestReset request) {
        try{
            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(request.getEmail());
            if (userOpt.isPresent()) {
                return ApiResponse.error(
                        "User with this email does not exist",
                        null
                );
            }

            User user = userOpt.get();
            otpRepository.expireUserUnverifiedOTPs(user, LocalDateTime.now());

            String otpCode = generateOTP();
            OTP otp = new OTP();
            otp.setUser(user);
            otp.setOtpcode(otpCode);
            otpRepository.save(otp);

            emailService.sendPasswordResetOTP(user,otpCode);

            return ApiResponse.success(
                    "OTP code has been sent to your email.",
                    null
            );
        } catch (Exception e){
            log.error("Password reset request error: ", e);
            return ApiResponse.error(
                    "Failed to send password reset OTP",
                    e.getMessage()
            );
        }
    }

    @Transactional
    public ApiResponse<?> resetPassword(ResetPasswordRequest request) {
        try{
            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(request.getEmail());
            if (userOpt.isEmpty()){
                return ApiResponse.error("User with this email does not exist",null);
            }
            User user = userOpt.get();

            Optional<OTP> otpOpt = otpRepository.findByUserAndIsVerifiedFalseOrderByCreatedAtDesc(user);
            if (otpOpt.isEmpty()){
                return ApiResponse.error(
                        "No valid verified OTP found",
                        null
                );
            }
            OTP otp = otpOpt.get();
            if (!otp.isVerified() || otp.isExpired()){
                return ApiResponse.error(
                        "Invalid or expired session",
                        null
                );
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            otpRepository.delete(otp);

            return ApiResponse.success(
                    "Password reset successfullly",
                    null
            );
        } catch (Exception e){
            log.error("Password reset request error: ", e);
            return ApiResponse.error(
                    "Failed to reset password",e.getMessage()
            );
        }
    }

    @Transactional
    public ApiResponse<?> changePassword(ChangePasswordRequest request,User currentUser) {
        try{
            if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())){
                return ApiResponse.error(
                        "Current password is incorrect",
                        null
                );
            }
            if (request.getOldPassword().equals(request.getNewPassword())){
                return ApiResponse.error(
                        "New password must be different from the current password",
                        null
                );
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())){
                return ApiResponse.error(
                        "New passwords do not match",
                        null
                );
            }
            currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(currentUser);

            return ApiResponse.success(
                    "Password changed successfully",
                    null
            );
        } catch (Exception e){
            log.error("Password change error: ", e);
            return ApiResponse.error(
                    "Failed to change password",e.getMessage()
            );
        }
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public ApiResponse<AuthResponse.AuthData> refreshToken(TokenRefreshRequest request){
        try{
            String refreshToken = request.getRefreshToken();

            if (!jwtUtil.validateToken(refreshToken)){
                return ApiResponse.error(
                        "Invalid or expired refresh token",
                        null
                );
            }
            String tokenType = jwtUtil.extractTokenType(refreshToken);
            if (!"refresh".equals(tokenType)) {
                return ApiResponse.error(
                        "Invalid token type",
                        null
                );
            }
            String username = jwtUtil.extractUsername(refreshToken);
            Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(username)
                    .or(() -> userRepository.findByEmailIgnoreCase(username));

            if (userOpt.isEmpty()){
                return ApiResponse.error(
                        "User not found",
                        null
                );
            }
            User user = userOpt.get();

            String newAccessToken = jwtUtil.generateAccessToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            AuthResponse.AuthData authData = AuthResponse.AuthData.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000)
                    .build();

            return ApiResponse.success(
                    "Token refreshed successfully",
                    authData
            );
        } catch (Exception e){
            log.error("Token refresh error: ", e);
            return ApiResponse.error(
                    "Failed to refresh token",e.getMessage()
            );
        }
    }

    public ApiResponse<?> logout(LogoutRequest request){
        try{
            String refreshToken = request.getRefreshToken();

            if (refreshToken == null || refreshToken.trim().isEmpty()){
                return ApiResponse.error(
                        "Refresh token is required",
                        null
                );
            }
            return ApiResponse.success(
                    "Logout successfull",
                    null
            );
        } catch (Exception e){
            log.error("Logout error: ", e);
            return ApiResponse.error(
                    "Logout failed",e.getMessage()
            );
        }
    }

}
