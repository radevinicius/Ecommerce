package com.ecommerce.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequestDTO(

        @NotBlank(message = "O nome do produto é obrigatório")
        String name,

        String description,

        @NotNull(message = "Preço é obrigatorio")
        @DecimalMin(value = "0.01", message = "Preço tem que ser aceima maior que 0")
        BigDecimal price,

        @NotNull(message = "Quantidade é obrigatoria")
        @Min(value = 0, message = "Quantdade deve ser maior que 0")
        Integer stockQuantity
) {






}
