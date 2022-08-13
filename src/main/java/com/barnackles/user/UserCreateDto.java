package com.barnackles.user;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.barnackles.user.User.USERNAME_PATTERN;

@Data
public class UserCreateDto {

    private Long id;
    @Column(unique = true)
    @Length(min = 5, message = "Your user name must have at least 5 characters")
    @Pattern(regexp = USERNAME_PATTERN, message = "Your user name must comprise only of letters, digits and underscore")
    @NotBlank(message = "Please provide a user name")
    private String userName;
    @Column(unique = true)
    @Email(message = "Please provide a valid Email")
    @NotBlank(message = "Please provide an email")
    private String email;
    @Length(min = 8, message = "Your password must have at least 8 characters")
    @NotBlank(message = "Please provide your password")
    private String password;

    public UserResponseDto userToResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(id);
        userResponseDto.setUserName(userName);
        userResponseDto.setEmail(email);
        return userResponseDto;
    }

    public User toUserToCreate() {
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    public User toUserToUpdate() {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setEmail(email);
        return user;
    }

}
