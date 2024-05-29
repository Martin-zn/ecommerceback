package com.martin.ecommerce.springecommerce.services;

import java.util.Date;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.martin.ecommerce.springecommerce.entities.LocalUser;

import javax.crypto.SecretKey;

@Service
@Slf4j
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
    private static final String RESET_PASSWORD_EMAIL_KEY = "RESET_PASSWORD_EMAIL";

    //Metodo para generar Token
    public String generateJWT(LocalUser user){
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(String.valueOf(1000*expiryInSeconds))))
                .issuer(issuer)
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    //Obtener la firma del token Encriptado
    public SecretKey getSignatureKey(){
        return Keys.hmacShaKeyFor(algorithmKey.getBytes());
    }

    //Validacion de token
    public boolean isTokenValid(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSignatureKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        }catch (Exception e){
            log.error("Token invalido, error: ".concat(e.getMessage()));
            return false;
        }
    }
    //Extraccion de todos los  Claims
    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignatureKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //Obtener un Claim
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = extractClaims(token);
        return claimsTFunction.apply(claims);
    }



    public String getUsername(String token){
        return getClaim(token, Claims::getSubject);
    }

    //--------------------------------------------------------------------VERIFICACION CON EMAIL--------------------------------------------------------------------//
    //Genero un metodo que genere un JWT, con el claim email_key
    public String generateVerificationJWT(LocalUser user){
        return Jwts.builder()
                .claim(VERIFICATION_EMAIL_KEY, user.getEmail())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(String.valueOf(1000*expiryInSeconds))))
                .issuer(issuer)
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Creo un Token para el reseteo de la clave
    public String generatePasswordResetJWT(LocalUser user){
        return Jwts.builder()
                        .claim(RESET_PASSWORD_EMAIL_KEY, user.getEmail())
                        .expiration(new Date(System.currentTimeMillis() + (1000*60*30)))
                        .issuer(issuer)
                        .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                        .compact();

    }

    //MEtodo para extraer el correo del token
    public String getResetPasswordEmail(String token){

        return getClaim(token, claims -> claims.get(RESET_PASSWORD_EMAIL_KEY, String.class));
        //Borrar si funciona
//        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
//        return JWT.decode(token).getClaim(RESET_PASSWORD_EMAIL_KEY).asString();
    }

}
