package com.barnackles.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginRestController {

    @PostMapping("/login")
    public void login(@RequestBody LoginDetails loginDetails ) {

    }
}
