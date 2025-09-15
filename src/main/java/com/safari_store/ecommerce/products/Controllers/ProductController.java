package com.safari_store.ecommerce.products.Controllers;

import com.safari_store.ecommerce.products.DTOS.ProductDTO;
import com.safari_store.ecommerce.products.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable){
        Page<ProductDTO> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id){
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductDTO>> getFeaturedProducts(){
        List<ProductDTO> featuredProducts = productService.getFeaturedProducts();
        return ResponseEntity.ok(featuredProducts);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(@RequestParam String keyword, Pageable pageable){
        Page<ProductDTO> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

}
