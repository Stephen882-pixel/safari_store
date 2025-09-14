package com.safari_store.ecommerce.users.models;

import com.safari_store.ecommerce.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "use_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @Column(nullable = false)
    private String country = "Kenya";

    @Column(nullable = false)
    private String county;

    @Column(nullable = false)
    private String constituency;

    @Column(nullable = false)
    private String town;

    private String estate;
    private String street;
    private String landmark;
    private String postalCode;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum AddressType{
        BILLING, SHIPPING, BOTH
    }
}
