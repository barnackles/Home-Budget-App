package com.barnackles.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.filter.CustomAuthorizationFilter;
import com.barnackles.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;

import static com.barnackles.filter.CustomAuthorizationFilter.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Controller
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final IAuthenticationFacade authenticationFacade;


    private final JwtUtil jwtUtil;


    /**
     * @return ResponseEntity<User>
     */
    @Secured("ROLE_USER")
    @GetMapping("/user/current")
    public ResponseEntity<UserResponseDto> findCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String userName = authentication.getName();

        User user = userService.findUserByUserName(userName);
        UserResponseDto userResponseDto = convertToResponseDto(user);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }


    /**
     * @param userCreateDto
     * @return ResponseEntity<UserResponseDto>
     */
    //email confirmation
    @PostMapping("/user")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {

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

    /**
     * @param userUpdateDto
     * @return ResponseEntity<UserResponseDto>
     */
    @Secured("ROLE_USER")
    @PutMapping("/user/update")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        Authentication authentication = authenticationFacade.getAuthentication();

        User persistentUser = userService.findUserByUserName(authentication.getName());
        UserResponseDto responseUser = convertToResponseDto(persistentUser);
        HttpStatus httpStatus = HttpStatus.CONFLICT;

        User user;
        try {
            user = convertUpdateDtoToUser(userUpdateDto);
            user.setId(persistentUser.getId());
            user.setPassword(persistentUser.getPassword());
            user.setActive(persistentUser.getActive());
            user.setRoles(persistentUser.getRoles());
            user.setBudgets(persistentUser.getBudgets());
            userService.updateUser(user);
            responseUser = convertToResponseDto(user);
            httpStatus = HttpStatus.OK;
        } catch (ParseException e) {
            log.error("unable to parse dto to entity error: {}", e.getMessage());
        }
        return new ResponseEntity<>(responseUser, httpStatus);
    }

    /**
     * @param userPasswordUpdateDto
     * @return ResponseEntity<UserResponseDto>
     */
    @Secured("ROLE_USER")
    @PutMapping("user/user-password")
    public ResponseEntity<String> updateUserPassword
    (@Valid @RequestBody UserPasswordUpdateDto userPasswordUpdateDto) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        String response = "Incorrect password";
        HttpStatus httpStatus = HttpStatus.PRECONDITION_FAILED;

        if (passwordEncoder.matches(userPasswordUpdateDto.getCurrentPassword(), user.getPassword())) {
            user.setPassword(userPasswordUpdateDto.getNewPassword());
            userService.updateUserPassword(user);
            response = "Password updated successfully";
            httpStatus = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    // secure unintentional deletion
    @Secured("ROLE_USER")
    @DeleteMapping("/user/current")
    public ResponseEntity<String> deleteUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        String message = String.format("User: %s successfully deleted ", user.getUserName());
        userService.deleteUser(user);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Secured("ROLE_USER")
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            try {
                String refresh_token = authorizationHeader.substring(TOKEN_PREFIX.length());
                JWTVerifier verifier = JWT.require(jwtUtil.getAlgorithm2()).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String userName = decodedJWT.getSubject();
                User user = userService.findUserByUserName(userName);
                if (user != null) {
                    new ObjectMapper().writeValue(response.getOutputStream(),
                            jwtUtil.generateTokenUponRefresh(user, request, response, refresh_token));
                }
            } catch (Exception e) {
                log.error("Error: {}", "RefreshToken Error");
                CustomAuthorizationFilter.setResponseHeader(response, e);
            }
        } else {
            throw new RuntimeException("No refresh token");
        }

    }


    /**
     * @param user
     * @return UserResponseDto
     * Entity to DTO conversion
     */
    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
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

    /**
     * @param userUpdateDto
     * @return User
     * UpdateDTO to Entity conversion
     */

    private User convertUpdateDtoToUser(UserUpdateDto userUpdateDto) throws ParseException {
        return modelMapper.map(userUpdateDto, User.class);
    }

}
