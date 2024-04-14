package com.richieoscar.artwoodcba.service;

import com.richieoscar.artwoodcba.domain.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final long serialVersionUID = 234234523523L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    public static final long JWT_TOKEN_REFRESH_VALIDITY = 10 * 60 * 60;

    @Value("${secret.key}")
    private String secretKey;

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {

        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }


    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    //generate token for user
    public String generateToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("firstName", customer.getFirstName());
        claims.put("lastName", customer.getLastName());
        claims.put("phone", customer.getPhone());
        claims.put("email",customer.getEmail());
        claims.put("role", customer.getRole());
        claims.put("customerId", customer.getId());
        return doGenerateToken(claims, customer.getEmail());
    }

    public String generateRefreshToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("firstName", customer.getFirstName());
        claims.put("lastName", customer.getLastName());
        claims.put("phone", customer.getPhone());
        claims.put("email",customer.getEmail());
        claims.put("role", customer.getRole());
        claims.put("customerId", customer.getId());
        return doGenerateRefreshToken(claims, customer.getEmail());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSecretKey(), SignatureAlgorithm.HS512).compact();
    }

    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_REFRESH_VALIDITY * 1000))
                .signWith(getSecretKey(), SignatureAlgorithm.HS512).compact();
    }

    private Key getSecretKey() {
        byte[] decode = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decode);

    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
