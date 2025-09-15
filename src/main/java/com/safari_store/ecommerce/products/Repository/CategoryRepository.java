package com.safari_store.ecommerce.products.models.Repository;

import com.safari_store.ecommerce.products.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findByActiveTrue();

    Page<Category> findByActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.active = true AND SIZE(c.products) > 0")
    List<Category> findActiveCategoriesWithProducts();

    boolean existsByName(String name);

}
