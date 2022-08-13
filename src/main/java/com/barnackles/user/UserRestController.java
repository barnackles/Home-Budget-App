package com.barnackles.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;

    /**
     * admin only
     * @return ResponseEntity<List<User>>
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> findAllUsers() {
         List<User> users = userService.findAll();
         return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponseDto> save(@Valid @RequestBody UserCreateDto userCreateDto) {

        User user;
        try {
            user = convertCreateDtoToUser(userCreateDto);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        userService.saveUser(user);
        UserResponseDto createdUser = convertToResponseDto(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //password change via separate controller
    @PutMapping("/user")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        User user;
        try {
            user = convertUpdateDtoToUser(userUpdateDto);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        User persistentUser = userService.findUserById(userUpdateDto.getId());
        user.setPassword(persistentUser.getPassword());
        user.setActive(persistentUser.getActive());
        user.setRoles(persistentUser.getRoles());
        user.setBudgets(persistentUser.getBudgets());
        user.setAssets(persistentUser.getAssets());

        userService.updateUser(user);
        UserResponseDto updatedUser = convertToResponseDto(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

//    @PutMapping("/user")
//    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody User user) {
//        userService.updateUser(user);
//        UserResponseDto updatedUser = user.userToResponseDto();
//        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
//    }




    @PostMapping("/test")
    public void firstUser() {
        User firstUser = new User();
        firstUser.setUserName("thorsten");
        firstUser.setEmail("throsten@gmail.com");
        firstUser.setPassword("testtest");
        userService.saveUser(firstUser);
    }

    /**
     * @param user
     * @return UserResponseDto
     * Entity to DTO conversion
     */
    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
        userResponseDto.setId(userService.findUserById(user.getId()).getId());
        return userResponseDto;
    }

    /**
     * @param userCreateDto
     * @return User
     * CreateDTO to Entity conversion
     */

    private User convertCreateDtoToUser(UserCreateDto userCreateDto) throws ParseException {
        return modelMapper.map(userCreateDto, User.class);
    }

    private User convertUpdateDtoToUser(UserUpdateDto userUpdateDto) throws ParseException {
        return modelMapper.map(userUpdateDto, User.class);
    }


    //    private User convertResponseDtoToUser(UserResponseDto userResponseDto) throws ParseException {
//        return modelMapper.map(userResponseDto, User.class);
//    }


//    public UserResponseDto userToResponseDto() {
//        UserResponseDto userResponseDto = new UserResponseDto();
//        userResponseDto.setId(id);
//        userResponseDto.setUserName(userName);
//        userResponseDto.setEmail(email);
//        return userResponseDto;
//    }


}
