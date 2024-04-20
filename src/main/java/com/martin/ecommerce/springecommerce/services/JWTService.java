package com.martin.ecommerce.springecommerce.services;

import java.util.Date;

import com.auth0.jwt.interfaces.DecodedJWT;
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
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
    private static final String RESET_PASSWORD_EMAIL_KEY = "RESET_PASSWORD_EMAIL";


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
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }

    //VERIFICACION CON EMAIL
    //Genero un metodo que genere un JWT, con el claim email_key, esto para generar una validacion por correo( por lo mismo de agrego springbot- email en el pom)
    public String generateVerificationJWT(LocalUser user){
        //Este metodo es muy parecido al principal para crear el JWT
        return JWT.create()
        .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000*expiryInSeconds)))
        .withIssuer(issuer)
        .sign(algorithm);
    }

    //Funcion para resetear la contraseña
    //Copiamos el metodo anterior
    public String generatePasswordResetJWT(LocalUser user){
        return JWT.create()
                //Creo un JWT, con el claim correspondiente y con el correo correspondente
                .withClaim(RESET_PASSWORD_EMAIL_KEY, user.getEmail())
                //Hardcodeo la duracion para hacerlo mas rapido, seran 30 min
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000*60*30)))
                //Defino el issuer y la sign
                .withIssuer(issuer)
                .sign(algorithm);
    }

    //MEtodo para extraer el correo del token
    public String getResetPasswordEmail(String token){
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return JWT.decode(token).getClaim(RESET_PASSWORD_EMAIL_KEY).asString();
    }

}
