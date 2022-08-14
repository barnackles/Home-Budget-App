package com.barnackles.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Data
public class JwtUtil {

    private Algorithm algorithm = Algorithm.HMAC256("secret".getBytes()); // nie powinno się przechowywać secretu w kodzie
    public Map<String, String> generateTokens(User user, HttpServletRequest request, HttpServletResponse response) {

        String access_token = JWT.create().withSubject(user.getUsername()).withExpiresAt(new Date(System.currentTimeMillis() + (3600000 / 6) )) //10 min
                .withIssuer(request.getRequestURL().toString()).withClaim("roles", user.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token = JWT.create().withSubject(user.getUsername()).withExpiresAt(new Date(System.currentTimeMillis() + (3600000 / 6) ))
                .withIssuer(request.getRequestURL().toString()).withClaim("roles", user.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        return tokens;
    }




}
