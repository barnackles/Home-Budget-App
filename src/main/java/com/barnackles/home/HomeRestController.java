package com.barnackles.home;

import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.user.User;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeRestController {

    private final IAuthenticationFacade authenticationFacade;
    private final UserService userService;






    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/user/home")
    public User home() {

        Authentication authentication = authenticationFacade.getAuthentication();

        return userService.findUserByUserName(authentication.getName());
    }



}
