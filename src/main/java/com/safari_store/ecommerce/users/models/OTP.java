package com.safari_store.ecommerce.users.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false,length = 6)
    private String otpcode;

    @CreatedDate
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isVerified;

    @PrePersist
    protected void onCreate() {
        if(expiresAt == null) {
            expiresAt = LocalDateTime.now().plusMinutes(10);
        }
    }

    public boolean isValid(){
        return LocalDateTime.now().isBefore(expiresAt) || LocalDateTime.now().isEqual(expiresAt);
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
