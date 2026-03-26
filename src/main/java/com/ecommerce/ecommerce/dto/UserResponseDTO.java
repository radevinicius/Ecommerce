package com.ecommerce.ecommerce.dto;

public record UserResponseDTO(
        Long id,
        String name,
        String email

) {
    @Override
    public Long id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String email() {
        return email;
    }
}
