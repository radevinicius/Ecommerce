package com.ecommerce.ecommerce.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ecommerce.ecommerce.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            try {
                DecodedJWT decodedJWT = jwtService.getDecodedJWT(token);

                String email = decodedJWT.getSubject();
                List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

                var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                System.out.println("Token inválido");
            }
        }

        filterChain.doFilter(request, response);
    }
}