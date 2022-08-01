package com.barnackles.home;

import com.barnackles.user.CurrentUser;
import com.barnackles.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {


//    @RequestMapping("/home")
//    @ResponseBody
//    public String home(@AuthenticationPrincipal CurrentUser customUser) {
//        User entityUser = customUser.getUser();
//        return entityUser.toString();
//    }

    @RequestMapping("/home")
    public String home() {

        return "home";
    }

}
