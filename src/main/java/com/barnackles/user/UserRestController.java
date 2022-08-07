package com.barnackles.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpl userService;


    @PostMapping("/register")
    public ResponseEntity<UserDto> save(@Valid @RequestBody User user) {
        userService.saveUser(user);
        UserDto createdUser = user.userToResponseEntity();
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/first")
    public void firstUser() {
        User firstUser = new User();
        firstUser.setUserName("James_Bond");
        firstUser.setEmail("oo7@gmail.com");
        firstUser.setPassword("testtest");
        userService.saveUser(firstUser);
    }


}
