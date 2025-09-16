package com.safari_store.ecommerce.cart.service;

import com.safari_store.ecommerce.cart.DTO.AddToCartRequest;
import com.safari_store.ecommerce.cart.DTO.CartDTO;
import com.safari_store.ecommerce.cart.DTO.CartItemDTO;
import com.safari_store.ecommerce.cart.Repository.CartItemRepository;
import com.safari_store.ecommerce.cart.Repository.CartRepository;
import com.safari_store.ecommerce.cart.models.Cart;
import com.safari_store.ecommerce.cart.models.CartItem;
import com.safari_store.ecommerce.products.Exceptions.ResourceNotFoundException;
import com.safari_store.ecommerce.products.Repository.ProductRepository;
import com.safari_store.ecommerce.products.Service.ProductService;
import com.safari_store.ecommerce.products.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if(!product.getCategory().getActive()){
            throw new IllegalArgumentException("Product is not available");
        }

        if(product.getStockQuantity() < request.getQuantity()){
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndItemId(userId, request.getProductId());

        CartItem cartItem;
        if(existingItem.isPresent()){
            cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if(product.getStockQuantity() < newQuantity){
                throw  new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
            }
            cartItem.setQuantity(newQuantity);
            cartItem.calculateSubTotal();
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(product.getPrice());
            cartItem.calculateSubTotal();
            cart.addItem(cartItem);
        }
        cartItem = cartItemRepository.save(cartItem);
        cart.calculateTotals();
        cartRepository.save(cart);
        return convertItemToDTO(cartItem);
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
