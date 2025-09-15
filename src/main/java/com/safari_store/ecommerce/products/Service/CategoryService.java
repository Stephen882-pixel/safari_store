package com.safari_store.ecommerce.products.Service;

import com.safari_store.ecommerce.products.DTOS.CategoryDTO;
import com.safari_store.ecommerce.products.Exceptions.ResourceNotFoundException;
import com.safari_store.ecommerce.products.models.Category;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.safari_store.ecommerce.products.models.Repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> getAllCategories(Pageable pageable){
        return categoryRepository.findByActiveTrue(pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public List<CategoryDTO> getAllActiveCategories(){
        return categoryRepository.findByActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public  CategoryDTO getCategoryById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDTO(category);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category with name " + categoryDTO.getName() + " already exists");
        }

        Category category = convertToEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO){
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!existingCategory.getName().equals(categoryDTO.getName()) &&
            categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category with name " + categoryDTO.getName() + " already exists");
        }

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        existingCategory.setImageUrl(categoryDTO.getImageUrl());
        existingCategory.setActive(categoryDTO.getActive());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }

    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setActive(false);
        categoryRepository.save(category);
    }

    public CategoryDTO convertToDTO(Category category){
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setActive(category.getActive());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }

    private Category convertToEntity(CategoryDTO dto){
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setActive(dto.getActive() != null ? dto.getActive() : true);
        return category;
    }

}
