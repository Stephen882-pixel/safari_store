package com.example.api.repository;

import com.example.api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.product.id = :productId")
    Optional<CartItem> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.id = :itemId")
    Optional<CartItem> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);
    
    void deleteByCartUserId(Long userId);
}