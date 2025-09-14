package com.safari_store.ecommerce.users.repository;

import com.safari_store.ecommerce.users.models.User;
import com.safari_store.ecommerce.users.models.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {
    Optional<PasswordResetRequest> findByToken(String token);

    Optional<PasswordResetRequest> findByTokenAndIsUsedFalse(String token);

    List<PasswordResetRequest> findByUserAndIsUsedFalse(User user);

    @Modifying
    @Query("UPDATE PasswordResetRequest p SET p.isUsed = true WHERE p.user = :user AND p.isUsed = false")
    void markUserRequestsAsUsed(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM PasswordResetRequest p WHERE p.expiresAt < :currentTime")
    void deleteExpiredRequests(@Param("currentTime") LocalDateTime currentTime);
}
