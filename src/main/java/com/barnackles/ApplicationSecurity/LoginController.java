package com.barnackles.ApplicationSecurity;

import com.barnackles.user.User;
import com.barnackles.user.UserService;
import com.barnackles.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserServiceImpl userService;

    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public String login() {

        return "login";
    }




}


