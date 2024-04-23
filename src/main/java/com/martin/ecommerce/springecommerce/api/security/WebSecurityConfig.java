package com.martin.ecommerce.springecommerce.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

//Declaro esta clase con la notacion  Configuration, para que pueda servir en la configuracion interna 
@Configuration
public class WebSecurityConfig {

    //Inyecto las dependencias de JWT RequestFilter, donde validamos la existencia de un JWT, 
    @Autowired
    private JWTRequestFilter jwtRequestFilter;

    //Declaro un Bean para poder utilizar el methodo en el framework completo
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        //Defino http como el representante la seguridad, sa este le atribullo las request autorizadas, esto lo hago con una exprecion lambda 
        //esto porque la versio simplificada esta deprecated, genero el objeto authz quien sera la cadena encargada de contener las autoizaciones
        //Agrego un filtro previo, para validar si es que la request http viene con el token JWT
        http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
        http.authorizeHttpRequests((authz) -> authz
            //    .anyRequest().permitAll()) //Esto es para probar
//        .requestMatchers(HttpMethod.GET,"/product","/product/**").permitAll()
//        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login","/auth/verify", "/error").permitAll()
        .requestMatchers("/product", "/auth/register", "/auth/login","/auth/verify", "/error",
                "/auth/forgot", "/auth/reset", "/websocket", "/websocket/**").permitAll()
        .anyRequest().authenticated())
        .csrf(AbstractHttpConfigurer::disable);
        //Entiendo que esta coniguracion se utiliza para evitar el crosed side scripting

        return http.build();
    }


}
