package com.safari_store.ecommerce.users.repository;

import com.safari_store.ecommerce.users.User;
import com.safari_store.ecommerce.users.models.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OTPRepository  extends JpaRepository<OTP, Long> {
    Optional<OTP> findByUserAndIsVerifiedFalseOrderByCreatedAtDesc(User user);

    List<OTP> findByUserAndIsVerifiedFalse(User user);

    Optional<OTP> findByUserAndOtpCodeAndIsVerifiedFalse(User user, String otpCode);

    @Query("SELECT o FROM OTP o WHERE o.user = :user AND o.isVerified = false ORDER BY o.createdAt DESC")
    Optional<OTP> findLatestUnverifiedOTPByUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE OTP o SET o.expiresAt = :expireTime WHERE o.user = :user AND o.isVerified = false")
    void expireUserUnverifiedOTPs(@Param("user") User user, @Param("expireTime") LocalDateTime expireTime);

    @Modifying
    @Query("DELETE FROM OTP o WHERE o.expiresAt < :currentTime")
    void deleteExpiredOTPs(@Param("currentTime") LocalDateTime currentTime);
}
