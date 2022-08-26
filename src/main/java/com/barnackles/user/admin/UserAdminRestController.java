package com.barnackles.user.admin;

import com.barnackles.budget.Budget;
import com.barnackles.user.User;
import com.barnackles.user.UserCreateDto;
import com.barnackles.user.UserServiceImpl;
import com.barnackles.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserAdminRestController {

    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    /**
     * admin only
     *
     * @return ResponseEntity<List < UserAdminResponseDto>>
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/users/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<List<UserAdminResponseDto>> findAllUsers(
            @PathVariable int pageNumber, @PathVariable int pageSize, @PathVariable String sortBy
    ) {


        List<User> users = userService.findAll(pageNumber, pageSize, sortBy);
        List<UserAdminResponseDto> userAdminResponseDtos = users
                .stream()
                .map(this::convertToAdminResponseDto)
                .toList();

        userAdminResponseDtos.forEach(
                userAdminResponseDto -> userAdminResponseDto.setBudgets
                        (getBudgetsMapFromUsersList(
                                        users, userAdminResponseDto.getId()
                                )
                        )
        );


        return new ResponseEntity<>(userAdminResponseDtos, HttpStatus.OK);
    }

    /**
     * @return ResponseEntity<UserAdminResponseDto>
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserAdminResponseDto> findUserById(@PathVariable Long id) {

        User user = userService.findUserById(id);

        UserAdminResponseDto userAdminResponseDto = convertToAdminResponseDto(user);
        userAdminResponseDto.setBudgets(getBudgetsMap(user));

        return new ResponseEntity<>(userAdminResponseDto, HttpStatus.OK);
    }

    /**
     * @param userCreateDto
     * @return ResponseEntity<UserResponseDto>
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/user")
    public ResponseEntity<UserAdminResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {

        User user;
        try {
            user = convertCreateDtoToUser(userCreateDto);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        userService.saveUser(user);
        UserAdminResponseDto userAdminResponseDto = convertToAdminResponseDto(user);
        return new ResponseEntity<>(userAdminResponseDto, HttpStatus.CREATED);
    }

    /**
     * @param userAdminUpdateDto
     * @return ResponseEntity<UserResponseDto>
     */
    @Secured("ROLE_ADMIN")
    @PutMapping("/user/{id}")
    public ResponseEntity<UserAdminResponseDto> updateUser(@Valid @RequestBody UserAdminUpdateDto userAdminUpdateDto, @PathVariable Long id) {

        User persistentUser = userService.findUserById(id);
        UserAdminResponseDto userAdminResponseDto = convertToAdminResponseDto(persistentUser);
        HttpStatus httpStatus = HttpStatus.PRECONDITION_FAILED;

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
     */
    //Admin only
    //add confirmation // secure unintentional deletion
    @Secured("ROLE_ADMIN")
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

    private User convertCreateDtoToUser(UserCreateDto userCreateDto) throws ParseException {
        return modelMapper.map(userCreateDto, User.class);
    }

    /**
     * @param userAdminUpdateDto
     * @return User
     * UpdateDTO to Entity conversion
     */


    private User convertUpdateDtoToUser(UserAdminUpdateDto userAdminUpdateDto) throws ParseException {
        return modelMapper.map(userAdminUpdateDto, User.class);
    }

    private HashMap<Long, String> getBudgetsMap(User user) {
        List<Budget> budgets = user.getBudgets();

        return (HashMap<Long, String>) budgets
                .stream()
                .collect(Collectors.toMap(Budget::getId, Budget::getBudgetName));
    }


    private HashMap<Long, String> getBudgetsMapFromUsersList(List<User> users, Long id) {

        for (User user : users) {
            if (id.equals(user.getId())) {

                List<Budget> budgets = user.getBudgets();

                return (HashMap<Long, String>) budgets
                        .stream()
                        .collect(Collectors.toMap(Budget::getId, Budget::getBudgetName));
            }
        }
        return null;
    }

}
