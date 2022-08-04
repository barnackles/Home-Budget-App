package com.barnackles.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpl userService;


    @PostMapping
    public void save(@RequestBody User user) {
        userService.saveUser(user);
    }


}
