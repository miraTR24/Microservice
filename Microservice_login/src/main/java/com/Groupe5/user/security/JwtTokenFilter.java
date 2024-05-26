package com.Groupe5.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = (String) request.getSession().getAttribute("token");

        if (token != null) {
            // Ajouter le token à l'en-tête de la requête
            request = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if (name.equals("Authorization")) {
                        return "Bearer " + token;
                    }
                    return super.getHeader(name);
                }
            };
        }

        filterChain.doFilter(request, response);
    }
}