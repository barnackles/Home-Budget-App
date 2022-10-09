package com.barnackles.ApplicationSecurity;

import com.barnackles.ApplicationSecurity.filter.CustomAuthenticationFilter;
import com.barnackles.ApplicationSecurity.filter.CustomAuthorizationFilter;
import com.barnackles.user.SpringDataUserDetailsService;
import com.barnackles.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Slf4j
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {


    private final String secret;
    private final String secret2;
    private String token;


    public ApplicationSecurityConfig(@Value("${jwt.secret") String secret, @Value("${jwt.secret2") String secret2) {
        this.secret = secret;
        this.secret2 = secret2;

    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringDataUserDetailsService userDetailsService() {
        return new SpringDataUserDetailsService();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(),
                new JwtUtil(secret, secret2), new ObjectMapper(), new LoginAttemptService());

//        http.requiresChannel()
//                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
//                .requiresSecure();

        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(
                "/login",
                "/user/token/refresh",
                "/user/forgotten-password/**",
                "/user/confirm/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v2/api-docs").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/register", "/user/set-new-password").permitAll();
        http.authorizeRequests().antMatchers("/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated();

        http.addFilter(customAuthenticationFilter);
        log.info("go to auth filter.");
        http.addFilterBefore(new CustomAuthorizationFilter(new JwtUtil(secret, secret2)),
                UsernamePasswordAuthenticationFilter.class);


    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

}



