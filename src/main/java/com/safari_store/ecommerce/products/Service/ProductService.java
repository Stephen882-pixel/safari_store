package com.safari_store.ecommerce.products.Service;

import com.safari_store.ecommerce.products.DTOS.ProductDTO;
import com.safari_store.ecommerce.products.Exceptions.ResourceNotFoundException;
import com.safari_store.ecommerce.products.models.Category;
import com.safari_store.ecommerce.products.models.Product;
import com.safari_store.ecommerce.products.models.Repository.CategoryRepository;
import com.safari_store.ecommerce.products.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private  final CategoryService categoryService;

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable){
        return productRepository.findByActiveTrue(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO>  searchProducts(String keyword,Pageable pageable){
        return productRepository.searchProducts(keyword,pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> filterProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Pageable pageable){
        return productRepository.filterProducts(categoryId,minPrice,maxPrice,inStock,pageable)
                .map(this::convertToDTO);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDTO.getCategoryId()));

        Product product = convertToEntity(productDTO);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDTO.getCategoryId()));

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStockQuantity(productDTO.getStockQuantity());
        existingProduct.setImageUrl(productDTO.getImageUrl());
        existingProduct.setAdditionalImages(productDTO.getAdditionalImagesUrls());
        existingProduct.setFeatured(productDTO.getFeatured());
        existingProduct.setActive(productDTO.getActive());
        existingProduct.setCategory(category);
        existingProduct.setTags(productDTO.getTags());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    public ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                // ✅ map entity list -> simple String list
                .additionalImagesUrls(
                        product.getAdditionalImages() == null
                                ? List.of()
                                : product.getAdditionalImages()
                                .stream()
                                //.map(Image::getUrl)
                                .toList()
                )
                .featured(product.isFeatured())
                .active(product.isActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .category(product.getCategory() != null
                        ? categoryService.convertToDTO(product.getCategory())
                        : null)
                .tags(product.getTags() != null ? new ArrayList<>(product.getTags()) : List.of())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setImageUrl(dto.getImageUrl());
        product.setAdditionalImages(dto.getAdditionalImagesUrls());
        product.setFeatured(dto.getFeatured());
        product.setActive(dto.getActive() != null ? dto.getActive(): true);
        product.setFeatured(dto.getFeatured() != null ? dto.getFeatured(): false);
        product.setTags(dto.getTags());
        return product;
    }
}
