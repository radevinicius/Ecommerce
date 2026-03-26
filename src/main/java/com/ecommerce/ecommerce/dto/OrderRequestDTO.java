package com.ecommerce.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequestDTO(

        @NotEmpty(message = "O pedido deve ter pelo menos um item")
        List<@Valid OrderItemRequestDTO> items

) {}