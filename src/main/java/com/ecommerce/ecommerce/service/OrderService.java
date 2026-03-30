package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.dto.*;
import com.ecommerce.ecommerce.entity.*;
import com.ecommerce.ecommerce.enums.OrderStatus;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        User loggedUser = getAuthenticatedUser();

        Order order = new Order();
        order.setUser(loggedUser);
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setItems(new ArrayList<>());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : request.items()) {

            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Produto não encontrado: " + itemDto.productId())
                    );

            if (product.getStockQuantity() < itemDto.quantity()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: " + product.getName()
                );
            }

            product.setStockQuantity(
                    product.getStockQuantity() - itemDto.quantity()
            );

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setPrice(product.getPrice());

            order.addItem(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubTotal());
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return toDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findMyOrders() {

        User loggedUser = getAuthenticatedUser();

        List<Order> myOrders = orderRepository.findByUserOrderByCreatedAtDesc(loggedUser);

        return myOrders.stream()
                .map(this::toDTO)
                .toList();
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado.")
                );
    }

    private OrderResponseDTO toDTO(Order order) {

        List<OrderItemResponseDTO> itemsDTO = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSubTotal()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getName(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                itemsDTO
        );
    }
}