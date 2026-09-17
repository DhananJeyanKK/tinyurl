package com.learn.tinyurl.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(null != authentication){

            SecretKey secretKey = Keys.hmacShaKeyFor("dhananjeyanlearningSDFWR@#$@#$QSFADFASDFG@#%@#SADFSDGSGDG@$#%@%@#SDDGDG".getBytes(StandardCharsets.UTF_8));
            String token = Jwts.builder()
                    .signWith(secretKey)
                    .expiration(new Date(System.currentTimeMillis() + 3000000))
                    .issuedAt(new Date())
                    .claim("auth", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                    .claim("username", authentication.getName())
                    .subject("jwt token")
                    .issuer("tiny url").compact();

            response.setHeader("Authorization", token);


        }

        filterChain.doFilter(request, response);



    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/url");
    }
}
