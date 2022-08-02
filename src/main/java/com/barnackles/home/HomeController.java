package com.barnackles.home;

import com.barnackles.user.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

//    @RequestMapping("/user/home")
//    @ResponseBody
//    public String home(@AuthenticationPrincipal CurrentUser customUser) {
//        User entityUser = customUser.getUser();
//        return entityUser.toString();
//    }
    @RequestMapping("/user/home")
    public String userHome(Model model, @AuthenticationPrincipal CurrentUser customUser) {
        model.addAttribute("customUserName", customUser.getUsername());
        return "userHome";
    }

}
