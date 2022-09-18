package com.barnackles.ApplicationSecurity.filter;

import com.barnackles.ApplicationSecurity.LoginAttemptService;
import com.barnackles.login.LoginDetails;
import com.barnackles.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;




@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    private final LoginAttemptService loginAttemptService;



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String ip = getClientIP(request);
        boolean isBlocked = loginAttemptService.isBlocked(ip);
        log.info("User is blocked: {}", isBlocked);
        if(isBlocked) {
            throw new AuthenticationException("blocked") {
            };
        }

        try {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            LoginDetails authRequest = objectMapper.readValue(sb.toString(), LoginDetails.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword()
            );

            log.info("login is {}", authRequest.getUsername());

            return authenticationManager.authenticate(authenticationToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException {
        User user = (User) auth.getPrincipal();

            final String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader == null) {
                loginAttemptService.loginSucceeded(request.getRemoteAddr());
            } else {
                loginAttemptService.loginSucceeded(xfHeader.split(",")[0]);
            }

        new ObjectMapper().writeValue(response.getOutputStream(), jwtUtil.generateTokens(user, request, response));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            loginAttemptService.loginFailed(request.getRemoteAddr());
        } else {
            loginAttemptService.loginFailed(xfHeader.split(",")[0]);
        }

        String message;

        if (failed.getMessage().equalsIgnoreCase("blocked")) {
            message = "User blocked due to many failed login attempts.";
            response.setStatus(HttpStatus.FORBIDDEN.value());
        } else {
            message = "Bad credentials.";
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), message);
        log.info("authentication unsuccessfull: {}", message);

    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
