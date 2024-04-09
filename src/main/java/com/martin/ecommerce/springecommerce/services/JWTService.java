package com.martin.ecommerce.springecommerce.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.martin.ecommerce.springecommerce.entities.LocalUser;

import jakarta.annotation.PostConstruct;

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;

    private static final String USERNAME_KEY = "USERNAME";
    private static final String EMAIL_KEY = "EMAIL";

    //Genero el JWT definiendo primero el algoritmo que utilizare para encriptarlo
    @PostConstruct
    public void PostConstruct(){
        algorithm = Algorithm.HMAC256(algorithmKey);
    }
    //Segundo genero un metodo, el cual utilizara el username sel usuario para crear el JWT, defino el claim, el tiempo de duracion, el creador y por ultimmo el algoritmo
    public String generateJWT(LocalUser user){
        return JWT.create()
        .withClaim(USERNAME_KEY, user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000*expiryInSeconds)))
        .withIssuer(issuer)
        .sign(algorithm);
    }

    //Genero un metodo encargado de decodificar un JWT, este resive un JWT y sabiendo cual es el claim desencripta el codigo
    public String getUsername(String token){
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }

    //VERIFICACION CON EMAIL
    //Genero un metodo que genere un JWT, con el claim email_key, esto para generar una validacion por correo( por lo mismo de agrego springbot- email en el pom)
    public String generateVerificationJWT(LocalUser user){
        //Este metodo es muy parecido al principal para crear el JWT
        return JWT.create()
        .withClaim(EMAIL_KEY, user.getEmail())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000*expiryInSeconds)))
        .withIssuer(issuer)
        .sign(algorithm);
    }

}
