package com.example.api.service;

import com.example.api.dto.*;
import com.example.api.entity.*;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    
    public OrderDTO createOrder(Long userId, CreateOrderRequest request) {
        // Get user's cart
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found or empty"));
        
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order with empty cart");
        }
        
        // Validate stock availability
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.getActive()) {
                throw new IllegalArgumentException("Product '" + product.getName() + "' is no longer available");
            }
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product '" + product.getName() + 
                        "'. Available: " + product.getStockQuantity() + ", Requested: " + cartItem.getQuantity());
            }
        }
        
        // Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        
        // Set shipping information
        order.setShippingName(request.getShippingName());
        order.setShippingEmail(request.getShippingEmail());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingCity(request.getShippingCity());
        order.setShippingState(request.getShippingState());
        order.setShippingPostalCode(request.getShippingPostalCode());
        order.setShippingCountry(request.getShippingCountry());
        
        // Set payment information
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());
        
        order = orderRepository.save(order);
        
        // Create order items from cart items
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.calculateSubtotal();
            
            order.addItem(orderItem);
            orderItemRepository.save(orderItem);
            
            // Update product stock
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        order.calculateTotals();
        order = orderRepository.save(order);
        
        // Clear the cart
        cart.getItems().clear();
        cart.calculateTotals();
        cartRepository.save(cart);
        
        return convertToDTO(order);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public OrderDTO getUserOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndIdWithItems(userId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return convertToDTO(order);
    }
    
    public OrderDTO cancelOrder(Long userId, Long orderId, CancelOrderRequest request) {
        Order order = orderRepository.findByUserIdAndIdWithItems(userId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.canBeCancelled()) {
            throw new IllegalArgumentException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        order.cancel(request.getReason());
        
        // Restore product stock
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }
        
        order = orderRepository.save(order);
        return convertToDTO(order);
    }
    
    @Transactional(readOnly = true)
    public OrderDTO trackOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndIdWithItems(userId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return convertToDTO(order);
    }
    
    // Admin methods
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return convertToDTO(order);
    }
    
    public OrderDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(request.getStatus());
        
        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }
        
        if (request.getEstimatedDelivery() != null) {
            order.setEstimatedDelivery(request.getEstimatedDelivery());
        }
        
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        
        // Set delivered timestamp if status changed to delivered
        if (request.getStatus() == OrderStatus.DELIVERED && previousStatus != OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
        order = orderRepository.save(order);
        return convertToDTO(order);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(this::convertToDTO);
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUserId());
        dto.setItems(order.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList()));
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setTotalItems(order.getTotalItems());
        
        // Shipping information
        dto.setShippingName(order.getShippingName());
        dto.setShippingEmail(order.getShippingEmail());
        dto.setShippingPhone(order.getShippingPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setShippingCity(order.getShippingCity());
        dto.setShippingState(order.getShippingState());
        dto.setShippingPostalCode(order.getShippingPostalCode());
        dto.setShippingCountry(order.getShippingCountry());
        
        // Order tracking
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setEstimatedDelivery(order.getEstimatedDelivery());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setCancelledAt(order.getCancelledAt());
        dto.setCancellationReason(order.getCancellationReason());
        
        // Payment information
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setNotes(order.getNotes());
        
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        return dto;
    }
    
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProduct(productService.convertToDTO(item.getProduct()));
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        
        // Historical product data
        dto.setProductName(item.getProductName());
        dto.setProductDescription(item.getProductDescription());
        dto.setProductImageUrl(item.getProductImageUrl());
        
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }
}