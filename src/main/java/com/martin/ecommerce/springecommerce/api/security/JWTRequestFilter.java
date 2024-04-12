package com.martin.ecommerce.springecommerce.api.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.repositories.UserRepository;
import com.martin.ecommerce.springecommerce.services.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");
        System.out.println("Estoy en el primer filtro");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            System.out.println("existe el token");
            String token = tokenHeader.substring(7);
            try{
                String username = jwtService.getUsername(token);
                System.out.println(jwtService.getUsername(token));
                System.out.println("obtuve el username del token " + username);
                Optional<LocalUser> opUser = userRepository.findByUsernameIgnoreCase(username);
                if(opUser.isPresent()){
                    LocalUser user = opUser.get();
                    if (user.isEmailVerified()){
                        System.out.println("Estoy autenticando");
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("AUTENTICADO");

                    }
                }

            } catch (JWTDecodeException ex){

            }
        }
        filterChain.doFilter((request), response);
        
    }

}
