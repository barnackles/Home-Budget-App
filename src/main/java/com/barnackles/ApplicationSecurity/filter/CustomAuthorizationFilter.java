package com.barnackles.ApplicationSecurity.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.barnackles.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    public static final String TOKEN_PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (("/login".equals(request.getServletPath()))
                || ("/user/token/refresh".equals(request.getServletPath()))
                || ("/user/forgotten-password/**".equals(request.getServletPath()))
                || ("/user/confirm/**".equals(request.getServletPath()))
                || ("/swagger-ui/**".equals(request.getServletPath()))
                || ("/swagger-resources/**".equals(request.getServletPath()))
                || ("/v2/api-docs".equals(request.getServletPath()))
                || ("/user/register".equals(request.getServletPath()))
                || ("/user/set-new-password".equals(request.getServletPath()))
        ) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
                try {
                    String token = authorizationHeader.substring(TOKEN_PREFIX.length());
                    JWTVerifier verifier = JWT.require(jwtUtil.getAlgorithm()).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String userName = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                    if (userName != null) {
                        UsernamePasswordAuthenticationToken authenticationToken
                                = new UsernamePasswordAuthenticationToken(userName,
                                null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error: {}", "Authorization Filter Error");
                    setResponseHeader(response, e);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    public static void setResponseHeader(HttpServletResponse response, Exception e) throws IOException {
        response.setHeader("error", e.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("error_message", e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
