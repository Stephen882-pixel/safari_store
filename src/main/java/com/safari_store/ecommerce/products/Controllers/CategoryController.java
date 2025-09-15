package com.safari_store.ecommerce.products.Controllers;

import com.safari_store.ecommerce.products.DTOS.CategoryDTO;
import com.safari_store.ecommerce.products.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(Pageable pageable){
        Page<CategoryDTO> categories = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categories);
    }
}
