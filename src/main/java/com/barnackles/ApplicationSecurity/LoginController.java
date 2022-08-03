package com.barnackles.ApplicationSecurity;

import com.barnackles.user.User;
import com.barnackles.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserServiceImpl userService;


    @RequestMapping("/login")
    public String login() {

        return "login";
    }

    @GetMapping("/registration")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String save(@Valid User user, BindingResult result) {
        if(result.hasErrors()) {
            return "/registration";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }





}
