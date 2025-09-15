package com.safari_store.ecommerce.users.models;

import com.safari_store.ecommerce.users.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PasswordResetRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String email;

    @Column(unique = true,nullable = false)
    private String token;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private boolean isUsed;

    private String oldPassword;

    private String newPassword;

    @PrePersist
    protected void onCreate() {
        if(token == null) {
            token = UUID.randomUUID().toString();
        }
        if(expiresAt == null) {
            expiresAt = LocalDateTime.now().plusHours(1);
        }
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid(){
        return !isUsed && !isExpired();
    }
}
