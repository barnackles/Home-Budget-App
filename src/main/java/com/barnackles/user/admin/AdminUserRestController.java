package com.barnackles.user.admin;

import com.barnackles.user.*;
import com.barnackles.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserRestController {

    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;



    /**
     * admin only
     * @return ResponseEntity<List<UserAdminResponseDto>>
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminResponseDto>> findAllUsers() {
         List<User> users = userService.findAll();
         List<UserAdminResponseDto> userAdminResponseDtos = users
                 .stream()
                 .map(this::convertToAdminResponseDto)
                 .toList();

         return new ResponseEntity<>(userAdminResponseDtos, HttpStatus.OK);
    }

    /**
     * @return ResponseEntity<UserAdminResponseDto>
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserAdminResponseDto> findUserById(@PathVariable Long id) {

        User user = userService.findUserById(id);
        UserAdminResponseDto userAdminResponseDto = convertToAdminResponseDto(user);
        return new ResponseEntity<>(userAdminResponseDto, HttpStatus.OK);
    }

    /**
     * @param userCreateDto
     * @return ResponseEntity<UserResponseDto>
     */

    @PostMapping("/user")
    public ResponseEntity<UserAdminResponseDto> userCreate(@Valid @RequestBody UserCreateDto userCreateDto) {

        User user;
        try {
            user = convertCreateDtoToUser(userCreateDto);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // check if exists
        userService.saveUser(user);
        UserAdminResponseDto userAdminResponseDto = convertToAdminResponseDto(user);
        return new ResponseEntity<>(userAdminResponseDto, HttpStatus.CREATED);
    }

    /**
     * @param userAdminUpdateDto
     * @return ResponseEntity<UserResponseDto>
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/user/{id}")
    public ResponseEntity<UserAdminResponseDto> UpdateUser(@Valid @RequestBody UserAdminUpdateDto userAdminUpdateDto, @PathVariable Long id) {

        User persistentUser = userService.findUserById(id);
        UserAdminResponseDto userAdminResponseDto = convertToAdminResponseDto(persistentUser);
        HttpStatus httpStatus = HttpStatus.PRECONDITION_FAILED;

        // check if username / email are not already in the database

        User user;
        try {
            user = convertUpdateDtoToUser(userAdminUpdateDto);
            user.setId(persistentUser.getId());
            user.setPassword(persistentUser.getPassword());
            userService.updateUser(user);
            userAdminResponseDto = convertToAdminResponseDto(user);
            httpStatus = HttpStatus.OK;
        } catch (ParseException e) {
            log.error("unable to parse dto to entity error: {}", e.getMessage());
        }
        return new ResponseEntity<>(userAdminResponseDto, httpStatus);
    }



    /**
     * @param id
     * @return ResponseEntity<String>
     *
     */
    //Admin only
    //add confirmation // secure unintentional deletion

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        User user = userService.findUserById(id);
        String message = String.format("User: %s successfully deleted ", user.getUserName());
        userService.deleteUser(user);
        return new ResponseEntity<>(message, HttpStatus.OK);


    }



    /**
     * @param user
     * @return UserAdminResponseDto
     * Entity to DTO conversion
     */
    private UserAdminResponseDto convertToAdminResponseDto(User user) {
        UserAdminResponseDto userAdminResponseDto = modelMapper.map(user, UserAdminResponseDto.class);
        return userAdminResponseDto;
    }

    /**
     * @param userCreateDto
     * @return User
     * CreateDTO to Entity conversion
     */

    //UserAdminCreateDTO ?
    private User convertCreateDtoToUser(UserCreateDto userCreateDto) throws ParseException {
        return modelMapper.map(userCreateDto, User.class);
    }

    /**
     * @param userAdminUpdateDto
     * @return User
     * UpdateDTO to Entity conversion
     */

    //userAdminUpdateDTO - all fields?

    private User convertUpdateDtoToUser(UserAdminUpdateDto userAdminUpdateDto) throws ParseException {
        return modelMapper.map(userAdminUpdateDto, User.class);
    }



//    @PostMapping("/test")
//    public void firstUser() {
//        User firstUser = new User();
//        firstUser.setUserName("thorsten");
//        firstUser.setEmail("throsten@gmail.com");
//        firstUser.setPassword("testtest");
//        userService.saveUser(firstUser);
//    }

}
