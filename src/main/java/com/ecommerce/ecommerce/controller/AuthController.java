package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.ecommerce.security.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO dto) {


        return jwtService.generateToken(dto.email());
    }
}
