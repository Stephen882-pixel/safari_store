package com.safari_store.ecommerce.cart.service;

import com.safari_store.ecommerce.cart.DTO.CartDTO;
import com.safari_store.ecommerce.cart.DTO.CartItemDTO;
import com.safari_store.ecommerce.cart.Repository.CartItemRepository;
import com.safari_store.ecommerce.cart.Repository.CartRepository;
import com.safari_store.ecommerce.cart.models.Cart;
import com.safari_store.ecommerce.cart.models.CartItem;
import com.safari_store.ecommerce.products.Repository.ProductRepository;
import com.safari_store.ecommerce.products.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

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

    private CartDTO convertToDTO(Cart cart){
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setItems(cart.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList()));
        dto.setTotalAmount(cart.getTotalAmount());
        dto.setTotalItems(cart.getTotalItems());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());
        return dto;
    }

    private CartItemDTO convertItemToDTO(CartItem item){
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProduct(productService.convertToDTO(item.getProduct()));
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }
}
