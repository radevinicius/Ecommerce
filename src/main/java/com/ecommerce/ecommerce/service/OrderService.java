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

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        // 1. IDENTIFICAR O USUÁRIO LOGADO PELO JWT
        User loggedUser = getAuthenticatedUser();

        // 2. INICIAR O PEDIDO
        Order order = new Order();
        order.setUser(loggedUser);
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setItems(new ArrayList<>()); // Inicializa a lista vazia

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. ANALISAR O CARRINHO (Loop nos itens que vieram do DTO)
        for (OrderItemRequestDTO itemDto : request.items()) {

            // Busca o produto no banco
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDto.productId()));

            // 4. VALIDAR ESTOQUE
            if (product.getStockQuantity() < itemDto.quantity()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + product.getName());
            }

            // Abater o estoque (Como estamos em @Transactional, o Hibernate salva isso no final sozinho)
            product.setStockQuantity(product.getStockQuantity() - itemDto.quantity());

            // 5. CONGELAR O PREÇO E CRIAR O ITEM DO PEDIDO
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItem(orderItem); // Amarra o item ao pedido
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setPrice(product.getPrice()); // 🔥 O Preço vem do BANCO, nunca do Front-end!

            // Adiciona na lista do pedido
            order.getItems().add(orderItem);

            // Soma o subtotal deste item ao total do pedido
            totalAmount = totalAmount.add(orderItem.getSubTotal());
        }

        // 6. FECHAR A CONTA E SALVAR
        order.setTotalAmount(totalAmount);

        // Salvar o pedido (Por causa do CascadeType.ALL na entidade, ele salva os OrderItems junto!)
        Order savedOrder = orderRepository.save(order);

        return toDTO(savedOrder);
    }

    // ========================================================================
    // MÉTODOS AUXILIARES E PRIVADOS (Sua organização de código)
    // ========================================================================

    // Método para pegar o email do usuário logado no Token JWT e buscar no banco
    private User getAuthenticatedUser() {
        // Pega o "subject" (geralmente email ou username) de dentro do Token JWT
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(username) // Ajuste para findByUsername se for o seu caso
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    // Método para converter a Entidade salva para DTO de Resposta
    private OrderResponseDTO toDTO(Order order) {

        // Converte a lista de OrderItem para OrderItemResponseDTO
        List<OrderItemResponseDTO> itemsDTO = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSubTotal()
                )).toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getName(), // Pega o nome do cliente
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                itemsDTO
        );
    }
}