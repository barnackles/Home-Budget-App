package com.barnackles.login;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LoginRestController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDetails loginDetails) {

        String message = "Logged in successfully.";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
