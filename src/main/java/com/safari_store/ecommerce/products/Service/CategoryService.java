package com.safari_store.ecommerce.products.Service;

import com.safari_store.ecommerce.products.DTOS.CategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.safari_store.ecommerce.products.models.Repository.CategoryRepository

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



}
