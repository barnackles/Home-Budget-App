package com.barnackles.ApplicationSecurity;

import com.barnackles.user.SpringDataUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
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

        http.
                authorizeRequests()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/test").permitAll()
//                .antMatchers("/api/home").permitAll()
////                .antMatchers("/api/login").permitAll()
                .antMatchers("/roles/**").permitAll()
//                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("api/user/**").hasAnyRole("USER", "ADMIN").anyRequest()
                .authenticated().and().csrf().disable().httpBasic();   //.formLogin()
//                .usernameParameter("userLogin")
//                .passwordParameter("password")
//                .loginPage("/login")//.loginProcessingUrl("/process-login")
//                .defaultSuccessUrl("/user/home")
//                .failureUrl("/login?error=true")
//                .and().logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/").and().exceptionHandling()
//                .accessDeniedPage("/access-denied");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }

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


