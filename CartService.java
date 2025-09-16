package com.example.api.service;

import com.example.api.dto.*;
import com.example.api.entity.Cart;
import com.example.api.entity.CartItem;
import com.example.api.entity.Product;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.CartItemRepository;
import com.example.api.repository.CartRepository;
import com.example.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    
    public CartItemDTO addItemToCart(Long userId, AddToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
        
        if (!product.getActive()) {
            throw new IllegalArgumentException("Product is not available");
        }
        
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
        }
        
        Cart cart = getOrCreateCart(userId);
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());
        
        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            
            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
            }
            
            cartItem.setQuantity(newQuantity);
            cartItem.calculateSubtotal();
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(product.getPrice());
            cartItem.calculateSubtotal();
            cart.addItem(cartItem);
        }
        
        cartItem = cartItemRepository.save(cartItem);
        cart.calculateTotals();
        cartRepository.save(cart);
        
        return convertItemToDTO(cartItem);
    }
    
    public CartItemDTO updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
        }
        
        cartItem.setQuantity(request.getQuantity());
        cartItem.calculateSubtotal();
        cartItem = cartItemRepository.save(cartItem);
        
        Cart cart = cartItem.getCart();
        cart.calculateTotals();
        cartRepository.save(cart);
        
        return convertItemToDTO(cartItem);
    }
    
    public void removeItemFromCart(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        Cart cart = cartItem.getCart();
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
    }
    
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        cart.getItems().clear();
        cart.calculateTotals();
        cartRepository.save(cart);
    }
    
    @Transactional(readOnly = true)
    public boolean hasItemsInCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        return cart != null && !cart.getItems().isEmpty();
    }
    
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }
    
    private CartDTO convertToDTO(Cart cart) {
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
    
    private CartItemDTO convertItemToDTO(CartItem item) {
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