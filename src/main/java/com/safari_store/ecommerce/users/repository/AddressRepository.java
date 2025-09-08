package com.safari_store.ecommerce.users.repository;

import com.safari_store.ecommerce.users.User;
import com.safari_store.ecommerce.users.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserOrderByCreatedAtDesc(User user);

    List<Address> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Address> findByIdAndUserId(Long id,Long userId);

    void deleteByIdAndUserId(Long id,Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.addressType = :addressType")
    List<Address> findByUserIdAndAddressType(@Param("userId") Long userId,
                                             @Param("addressType") Address.AddressType addressType);
}
