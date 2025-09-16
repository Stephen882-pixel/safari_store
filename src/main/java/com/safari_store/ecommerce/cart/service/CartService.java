package com.safari_store.ecommerce.cart.service;

import com.safari_store.ecommerce.cart.DTO.CartDTO;
import com.safari_store.ecommerce.cart.Repository.CartItemRepository;
import com.safari_store.ecommerce.cart.Repository.CartRepository;
import com.safari_store.ecommerce.cart.models.Cart;
import com.safari_store.ecommerce.products.Repository.ProductRepository;
import com.safari_store.ecommerce.products.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public CartDTO getUserCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return convertToDTO(cart);
    }
}
