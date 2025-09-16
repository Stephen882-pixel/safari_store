package com.safari_store.ecommerce.order.service;

import com.safari_store.ecommerce.cart.Repository.CartRepository;
import com.safari_store.ecommerce.cart.models.Cart;
import com.safari_store.ecommerce.cart.models.CartItem;
import com.safari_store.ecommerce.order.DTOS.OrderDTO;
import com.safari_store.ecommerce.order.DTOS.OrderItemDTO;
import com.safari_store.ecommerce.order.DTOS.Request.CancelOrderRequest;
import com.safari_store.ecommerce.order.DTOS.Request.CreateOrderRequest;
import com.safari_store.ecommerce.order.OrderStatus;
import com.safari_store.ecommerce.order.Repository.OrderItemRepository;
import com.safari_store.ecommerce.order.Repository.OrderRepository;
import com.safari_store.ecommerce.order.models.Order;
import com.safari_store.ecommerce.order.models.OrderItem;
import com.safari_store.ecommerce.products.Exceptions.ResourceNotFoundException;
import com.safari_store.ecommerce.products.Repository.ProductRepository;
import com.safari_store.ecommerce.products.Service.ProductService;
import com.safari_store.ecommerce.products.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderDTO createOrder(Long userId, CreateOrderRequest request){
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found or empty"));

        if(cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Cannot create order with an empty cart");
        }

        for(CartItem cartItem : cart.getItems()){
            Product product = cartItem.getProduct();
            if(!product.getCategory().getActive()){
                throw new IllegalArgumentException("Product ' " + product.getName() + "' is no longer available");
            }
            if(product.getStockQuantity() < cartItem.getQuantity()){
                throw  new IllegalArgumentException("Insufficient stock for product ' " + product.getName() +
                        " '. Available: " + product.getStockQuantity() + ", Requested: " + cartItem.getQuantity() );
            }
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);

        order.setShippingName(request.getShippingName());
        order.setShippingEmail(request.getShippingEmail());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingCity(request.getShippingCity());
        order.setShippingState(request.getShippingState());
        order.setShippingPostalCode(request.getShippingPostalCode());
        order.setShippingCountry(request.getShippingCountry());

        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());

        order = orderRepository.save(order);

        for(CartItem cartItem : cart.getItems()){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.calculateSubtotal();

            order.addItem(orderItem);
            orderItemRepository.save(orderItem);

            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        order.calculateTotals();
        order = orderRepository.save(order);

        cart.getItems().clear();
        cart.calculateTotals();
        cartRepository.save(cart);

        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getUsersOrders(Long userId, Pageable pageable){
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long userId,Long orderId){
        Order order = orderRepository.findByUserIdAndIdWithItems(userId,orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return convertToDTO(order);
    }

    public OrderDTO cancelOrder(Long userId, Long orderId, CancelOrderRequest request){
        Order order = orderRepository.findByUserIdAndIdWithItems(userId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if(!order.canBeCancelled()){
            throw new IllegalArgumentException("Order can not be cancelled in the current status: " + order.getStatus());
        }

        order.cancel(request.getReason());
        for(OrderItem orderItem : order.getItems()){
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }

        order = orderRepository.save(order);
        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order){
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


        dto.setShippingName(order.getShippingName());
        dto.setShippingEmail(order.getShippingEmail());
        dto.setShippingPhone(order.getShippingPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setShippingCity(order.getShippingCity());
        dto.setShippingState(order.getShippingState());
        dto.setShippingPostalCode(order.getShippingPostalCode());
        dto.setShippingCountry(order.getShippingCountry());

        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setEstimatedDelivery(order.getEstimatedDelivery());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setCancelledAt(order.getCancelledAt());
        dto.setCancelReason(order.getCancellationReason());


        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setNotes(order.getNotes());

        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        return dto;
    }



    private OrderItemDTO convertItemToDTO(OrderItem item){
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProduct(productService.convertToDTO(item.getProduct()));
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());


        dto.setProductName(item.getProductName());
        dto.setProductDescription(item.getProductDescription());
        dto.setProductImageUrl(item.getProductImageUrl());

        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }
}
