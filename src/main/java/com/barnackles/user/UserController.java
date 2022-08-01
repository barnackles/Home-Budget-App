package com.barnackles.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

//    @RequestMapping("/add")
//    @ResponseBody
//    public String add() {
//
//        User user = new User();
//        user.setUserName("tester");
//        user.setEmail("test60@gmail.com");
//        user.setPassword("sometest!");
//        user.setActive(true);
//        userService.saveUser(user);
//
//        return user.toString();
//    }



}
