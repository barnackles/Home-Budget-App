package com.barnackles.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.barnackles.role.Role;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Data
public class JwtUtil {

    private static final Date TEN_MINUTES_IN_MILLISECONDS = new Date(System.currentTimeMillis() + (3600000 / 6));
    private static final Date ONE_HOUR_IN_MILLISECONDS = new Date(System.currentTimeMillis() + (3600000));
    private final String secret;
    private final String secret2;
    private Date tokenExpirationTime;
    private Date refreshTokenActivationTime;
    private Date refreshTokenExpirationTime;
    private Algorithm algorithm;
    private Algorithm algorithm2;



    public JwtUtil(@Value("${jwt.secret") String secret, @Value("${jwt.secret2") String secret2) {
        this.secret = secret;
        this.secret2 = secret2;
        this.tokenExpirationTime = TEN_MINUTES_IN_MILLISECONDS;
        this.refreshTokenActivationTime = tokenExpirationTime;
        this.refreshTokenExpirationTime = ONE_HOUR_IN_MILLISECONDS;
        this.algorithm = Algorithm.HMAC512(secret.getBytes());
        this.algorithm2 = Algorithm.HMAC512(secret2.getBytes());
    }


    public Map<String, String> generateTokens(User user, HttpServletRequest request, HttpServletResponse response) {

        String access_token = JWT.create().withSubject(user.getUsername()).withExpiresAt(tokenExpirationTime)
                .withIssuer(request.getRequestURL().toString()).withClaim("roles", user.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token = JWT.create().withSubject(user.getUsername()).withExpiresAt(refreshTokenExpirationTime)
                .withIssuer(request.getRequestURL().toString()).withNotBefore(refreshTokenActivationTime).withClaim("roles", user.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm2);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        return tokens;
    }

    public Map<String, String> generateTokenUponRefresh(com.barnackles.user.User user, HttpServletRequest request, HttpServletResponse response,
                                                        String refresh_token) {

        String access_token = JWT.create().withSubject(user.getUserName()).withExpiresAt(tokenExpirationTime)
                .withIssuer(request.getRequestURL().toString()).withClaim("roles", user.getRoles()
                        .stream().map(Role::getRole).collect(Collectors.toList()))
                .sign(algorithm);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        return tokens;
    }




}
