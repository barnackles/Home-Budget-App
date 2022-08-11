package com.barnackles.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpl userService;


    @GetMapping("/users") // admin only
    public ResponseEntity<List<User>> findAllUsers() {
         List<User> users = userService.findAll();
         return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/user")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);

        UserResponseDto updatedUser = user.userToResponseDto();
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> save(@Valid @RequestBody User user) {
        userService.saveUser(user);
        UserResponseDto createdUser = user.userToResponseDto();
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }



//    @PostMapping("/test")
//    public void firstUser() {
//        User firstUser = new User();
//        firstUser.setUserName("James_Bond");
//        firstUser.setEmail("oo7@gmail.com");
//        firstUser.setPassword("testtest");
//        userService.saveUser(firstUser);
//    }


}
