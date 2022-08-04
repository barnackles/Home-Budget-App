package com.barnackles.home;

import com.barnackles.user.CurrentUser;
import com.barnackles.user.User;
import com.barnackles.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeRestController {

    private final UserServiceImpl userService;

//    @RequestMapping("/home")
//    @ResponseBody
//    public String home(@AuthenticationPrincipal CurrentUser customUser) {
//        User entityUser = customUser.getUser();
//        return entityUser.toString();
//    }

    @RequestMapping("/api/home")
    public String home() {

        return "home";
    }

    @GetMapping("/api/test")
    public String test() {
        return "test";
    }

    @RequestMapping("/user/home")
    public User home(@AuthenticationPrincipal CurrentUser customUser) {
        return customUser.getUser();
    }

//    @RequestMapping("/user/home/{username}")
//    public User home(@PathVariable String username) {
//        return userService.findUserByUserName(username);
//    }


}
