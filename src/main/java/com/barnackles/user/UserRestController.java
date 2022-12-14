package com.barnackles.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.ApplicationSecurity.filter.CustomAuthorizationFilter;
import com.barnackles.confirmationToken.ConfirmationToken;
import com.barnackles.confirmationToken.ConfirmationTokenService;
import com.barnackles.util.JwtUtil;
import com.barnackles.validator.uuid.ValidUuidString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.barnackles.ApplicationSecurity.filter.CustomAuthorizationFilter.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpl userService;

    private final ConfirmationTokenService confirmationTokenService;
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
     * @param userCreateDto user create data transfer object
     * @return ResponseEntity<UserResponseDto>
     */

    @PostMapping("/register")
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
     * @param userUpdateDto user update data transfer object
     * @return ResponseEntity<UserResponseDto>
     */
    @Secured("ROLE_USER")
    @PutMapping("/user/update")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        Authentication authentication = authenticationFacade.getAuthentication();

        User persistentUser = userService.findUserByUserName(authentication.getName());
        UserResponseDto responseUser = convertToResponseDto(persistentUser);
        HttpStatus httpStatus = HttpStatus.CONFLICT;

        if(persistentUser.getUserName().equals(userUpdateDto.getUserName())
        && persistentUser.getEmail().equals(userUpdateDto.getEmail())) {

            return new ResponseEntity<>(responseUser, httpStatus);
        }

        if (userService.emailCheck(userUpdateDto.getEmail(), persistentUser) &&
                userService.usernameCheck(userUpdateDto.getUserName(), persistentUser)) {
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
        return new ResponseEntity<>(responseUser, httpStatus);
    }

    /**
     * @param userPasswordUpdateDto password update data transfer object
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

    /**
     * @param userForgottenPasswordDto e-mail Dto
     * @return ResponseEntity<String>
     */
    @PostMapping("/forgotten-password")
    public ResponseEntity<String> forgottenPasswordRequest(@Valid @RequestBody UserForgottenPasswordDto userForgottenPasswordDto) {

        //check for null
        Optional<User> userOptional = userService.findUserByEmailOpt(userForgottenPasswordDto.getEmail());
        userOptional.ifPresent(userService::sendResetPasswordTokenToUser);
        String message = "If there is an account registered with the e-mail you have provided," +
                " we will send you reset password link to your e-mail.";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @Secured("ROLE_USER")
    @DeleteMapping("/user/current")
    public ResponseEntity<String> deleteUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        String message = String.format("Confirmation email sent to: %s", user.getEmail());
        userService.sendDeleteConfirmationToken(user);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


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
     * @param token registration token in the form of string
     * @return ResponseEntity
     * Accepts registration confirmation token and activates user.
     */

    @GetMapping("/confirm/registration/{token}")
    @ApiIgnore
    public ResponseEntity<String> confirmUserRegistration(@PathVariable @ValidUuidString @NotBlank String token) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message;
        try {
            UUID UuidToken = UUID.fromString(token);
            ConfirmationToken confirmationToken = confirmationTokenService.findConfirmationTokenByToken(UuidToken);
            LocalDateTime now = LocalDateTime.now();
            if (confirmationToken.getConfirmationTime() != null) {
                message = "Your account has already been activated.";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            if (now.isBefore(confirmationToken.getExpirationTime())
                && now.isAfter(confirmationToken.getCreationTime())) {

                confirmationToken.setConfirmationTime(now);
                confirmationTokenService.updateConfirmationToken(confirmationToken);
                User userToActivate = confirmationToken.getUser();
                userToActivate.setActive(true);
                userService.updateUser(userToActivate);
                message = "Your account has been confirmed.";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            message = "Confirmation token expired.";
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            message = "Invalid token.";
            return new ResponseEntity<>(message, httpStatus);
        }
    }

    /**
     * @param token deletion token
     * @return ResponseEntity
     * Accepts deletion confirmation token and deletes user.
     */

    @GetMapping("/confirm/deletion/{token}")
    @ApiIgnore
    public ResponseEntity<String> confirmUserDeletion(@PathVariable @ValidUuidString @NotBlank String token) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message;
        try {
            UUID UuidToken = UUID.fromString(token);
            ConfirmationToken confirmationToken = confirmationTokenService.findConfirmationTokenByToken(UuidToken);
            LocalDateTime now = LocalDateTime.now();
            if (confirmationToken.getConfirmationTime() != null) {
                message = "Your account has already been deleted.";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            if (now.isBefore(confirmationToken.getExpirationTime())
                    && now.isAfter(confirmationToken.getCreationTime())) {

                confirmationToken.setConfirmationTime(now);
                confirmationTokenService.updateConfirmationToken(confirmationToken);
                User userToDelete = confirmationToken.getUser();
                confirmationTokenService.deleteConfirmationToken(confirmationToken);
                userService.deleteUser(userToDelete);

                message = "Account deleted successfully.";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            message = "Confirmation token expired.";
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            message = "Invalid token.";
            return new ResponseEntity<>(message, httpStatus);
        }
    }

    /**
     * @param token password reset token
     * @return ResponseEntity
     * Accepts deletion confirmation token and deletes user.
     */

    @GetMapping("/confirm/password-reset/{token}")
    @ApiIgnore
    public ResponseEntity<String> confirmResetPassword(@PathVariable @ValidUuidString @NotBlank String token) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message;
        try {
            UUID UuidToken = UUID.fromString(token);
            ConfirmationToken confirmationToken = confirmationTokenService.findConfirmationTokenByToken(UuidToken);
            LocalDateTime now = LocalDateTime.now();
            if (confirmationToken.getConfirmationTime() != null) {
                message = "Your password has already been reset.";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            if (now.isBefore(confirmationToken.getExpirationTime())
                    && now.isAfter(confirmationToken.getCreationTime())) {

                confirmationToken.setConfirmationTime(now);
                confirmationTokenService.updateConfirmationToken(confirmationToken);
                User userToResetPassword = confirmationToken.getUser();

                String tempPassword = String.valueOf(Generators.timeBasedGenerator().generate());
                userToResetPassword.setPassword(tempPassword);
                userService.resetUserPassword(userToResetPassword);

                message = "Password has been reset. You will receive a link to set your new password.";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            message = "Confirmation token expired.";
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            message = "Invalid token.";
            return new ResponseEntity<>(message, httpStatus);
        }
    }

    /**
     * @param userSetNewPasswordDto object of type UserSetNewPasswordDto
     * @return ResponseEntity
     * Accepts deletion confirmation token and deletes user.
     */

    @PostMapping("/set-new-password")
    public ResponseEntity<String> setNewPassword(@Valid @RequestBody UserSetNewPasswordDto userSetNewPasswordDto) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message;
        try {
            UUID UuidToken = UUID.fromString(userSetNewPasswordDto.getToken());
            ConfirmationToken confirmationToken = confirmationTokenService.findConfirmationTokenByToken(UuidToken);
            LocalDateTime now = LocalDateTime.now();
            if (confirmationToken.getConfirmationTime() != null) {
                message = "This token has been already used.";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            if (now.isBefore(confirmationToken.getExpirationTime())
                    && now.isAfter(confirmationToken.getCreationTime())) {

                confirmationToken.setConfirmationTime(now);
                confirmationTokenService.updateConfirmationToken(confirmationToken);
                User userToSetNewPassword = confirmationToken.getUser();
                userToSetNewPassword.setPassword(userSetNewPasswordDto.getNewPassword());
                userService.updateUserPassword(userToSetNewPassword);

                message = "New password has been set.";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            message = "Confirmation token expired.";
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            message = "Invalid token.";
            return new ResponseEntity<>(message, httpStatus);

        }
    }

    /**
     * @param user object of type User
     * @return UserResponseDto
     * Entity to DTO conversion
     */
    private UserResponseDto convertToResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }


    /**
     * @param userCreateDto object of type UserCreateDto
     * @return User
     * CreateDTO to Entity conversion
     */

    private User convertCreateDtoToUser(UserCreateDto userCreateDto) throws ParseException {
        return modelMapper.map(userCreateDto, User.class);
    }

    /**
     * @param userUpdateDto object of type UserUpdateDto
     * @return User
     * UpdateDTO to Entity conversion
     */

    private User convertUpdateDtoToUser(UserUpdateDto userUpdateDto) throws ParseException {
        return modelMapper.map(userUpdateDto, User.class);
    }

}
