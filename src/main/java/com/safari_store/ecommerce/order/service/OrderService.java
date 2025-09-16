package com.safari_store.ecommerce.order.service;

import com.safari_store.ecommerce.cart.Repository.CartRepository;
import com.safari_store.ecommerce.order.Repository.OrderItemRepository;
import com.safari_store.ecommerce.order.Repository.OrderRepository;
import com.safari_store.ecommerce.products.Repository.ProductRepository;
import com.safari_store.ecommerce.products.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderDTO
}
