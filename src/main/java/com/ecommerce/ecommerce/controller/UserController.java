package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.ecommerce.dto.UserRequestDTO;
import com.ecommerce.ecommerce.dto.UserResponseDTO;
import com.ecommerce.ecommerce.entity.User;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/me")
    public UserResponseDTO getMe(Authentication authentication) {
        User user = service.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }


    @PostMapping
    public ResponseEntity<UserResponseDTO> create(
            @RequestBody @Valid UserRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid UserRequestDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
