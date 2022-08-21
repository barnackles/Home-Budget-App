package com.barnackles.ApplicationSecurity;

import com.barnackles.filter.CustomAuthenticationFilter;
import com.barnackles.filter.CustomAuthorizationFilter;
import com.barnackles.user.SpringDataUserDetailsService;
import com.barnackles.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {


    private final String secret;
    private final String secret2;

//    private final UserRepository userRepository;


    public ApplicationSecurityConfig(@Value("${jwt.secret") String secret, @Value("${jwt.secret2") String secret2) {
        this.secret = secret;
        this.secret2 = secret2;
//        this.userRepository = userRepository;
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
                new JwtUtil(secret, secret2));

            http.csrf().disable();
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.authorizeRequests().antMatchers(
                    "/login",
                    "/user/token/refresh",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/v2/api-docs").permitAll();

            http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/user").permitAll();
            http.authorizeRequests()
                    .antMatchers("admin/**").hasRole( "ADMIN")
                    .antMatchers("/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated();

            http.addFilter(customAuthenticationFilter);
            http.addFilterBefore(new CustomAuthorizationFilter(new JwtUtil(secret, secret2)),
                    UsernamePasswordAuthenticationFilter.class);


//            http.csrf().disable();
//            http
//                .authorizeRequests()
////                .antMatchers("/api/swagger-ui/**").permitAll()
////                .antMatchers("/api/v2/api-docs").permitAll()
////                .antMatchers("/api/token/refresh").permitAll()
////                .antMatchers("api/**").hasAnyRole("USER", "ADMIN")
//                .anyRequest()
////                .permitAll()
//               .authenticated()
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .addFilter(customAuthenticationFilter)
//                .addFilterBefore(new CustomAuthorizationFilter(new JwtUtil(secret, secret2)),
//                            UsernamePasswordAuthenticationFilter.class);



    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



//    @Bean
//    public UniqueEmailValidator uniqueEmailValidator() {
//        return new UniqueEmailValidator(userRepository);
//    }
//
//    @Bean
//    public UniqueUserNameValidator uniqueUserNameValidator() {
//        return new UniqueUserNameValidator(userRepository);
//    }
    




}


//
//@Configuration
//@RequiredArgsConstructor
//public class ApplicationSecurityConfig {
//
////    private final AuthenticationConfiguration authenticationConfiguration;
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SpringDataUserDetailsService userDetailsService() {
//        return new SpringDataUserDetailsService();
//
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.
//                authorizeRequests()
//                .antMatchers("/home").permitAll()
//                .antMatchers("/login").permitAll()
//                .antMatchers("/registration").permitAll()
////                .antMatchers("/admin/**").hasRole("ADMIN")
//                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN").anyRequest()
//                .authenticated().and().csrf().disable().formLogin()
//                //.loginPage("/login") //loginProcessingUrl("/login-perform")
//                .defaultSuccessUrl("/user/home")
//                .failureUrl("/login?error=true")
//                .usernameParameter("user_name")
//                .passwordParameter("password")
//                .and().logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/login").and().exceptionHandling()
//                .accessDeniedPage("/access-denied");
//
//        return http.build();
//    }
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
//    }
//
////    @Bean
////    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
////        return authenticationConfiguration.getAuthenticationManager();
////    }
//
//
//
//}


