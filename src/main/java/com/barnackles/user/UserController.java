package com.barnackles.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;


import javax.validation.Valid;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;


    @PostMapping("/save")
    public String save(@Valid User user, BindingResult result) {
        if(result.hasErrors()) {
            return "/registration";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }


}
