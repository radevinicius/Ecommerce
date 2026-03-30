package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.OrderRequestDTO;
import com.ecommerce.ecommerce.dto.OrderResponseDTO;
import com.ecommerce.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderRequestDTO request) {
        OrderResponseDTO response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> findMyOrders() {
        List<OrderResponseDTO> response = orderService.findMyOrders();
        return ResponseEntity.ok(response);
    }
}