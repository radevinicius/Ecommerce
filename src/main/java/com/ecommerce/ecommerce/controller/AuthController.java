package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.ecommerce.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        return jwtService.generateToken(authentication);
    }
}
