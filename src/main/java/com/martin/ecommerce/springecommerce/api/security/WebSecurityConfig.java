package com.martin.ecommerce.springecommerce.api.security;

import com.martin.ecommerce.springecommerce.api.security.filters.JwtAuthenticationFilter;
import com.martin.ecommerce.springecommerce.api.security.filters.JwtAuthorizationFilter;
import com.martin.ecommerce.springecommerce.services.JWTService;
import com.martin.ecommerce.springecommerce.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//Declaro esta clase con la notacion  Configuration, para que pueda servir en la configuracion interna 
@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)//Proximamente deprecate, o ya deprecated
public class WebSecurityConfig {

        @Autowired
        UserDetailsServiceImpl userDetailsService;

        @Autowired
        JWTService jwtService;

        @Autowired
        JWTRequestFilter jwtRequestFilter;

        @Autowired
        JwtAuthorizationFilter jwtAuthorizationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception{

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
            jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);

            //Defino http como el representante la seguridad, sa este le atribullo las request autorizadas, esto lo hago con una exprecion lambda
            //esto porque la versio simplificada esta deprecated, genero el objeto authz quien sera la cadena encargada de contener las autoizaciones
            //Agrego un filtro previo, para validar si es que la request http viene con el token JWT
            http.addFilter(jwtAuthenticationFilter);
            http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
            http.authorizeHttpRequests((authz) -> authz
                            .requestMatchers("/product", "/auth/register", "/auth/login","/auth/verify", "/error",
                                    "/auth/forgot", "/auth/reset", "/websocket", "/websocket/**", "/prueba/hello", "/login").permitAll()
                            .requestMatchers("/prueba/admin").hasRole("ADMIN")
                            .requestMatchers("/prueba/user").hasRole("USER")
                            .anyRequest().authenticated())
                            .csrf(AbstractHttpConfigurer::disable);
            http.sessionManagement(session -> {
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            });

            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsServiceImpl userDetailsService) throws Exception {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder());

            http.authenticationProvider(authProvider);

            return http.getSharedObject(AuthenticationManagerBuilder.class)
                    .authenticationProvider(authProvider)
                    .build();
        }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }









}
