package com.barnackles.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
//        user.setUserName("tester2");
//        user.setEmail("test2@gmail.com");
//        user.setPassword("testtest");
//        user.setActive(true);
//        userService.saveUser(user);
//
//        return user.toString();
//    }



}
