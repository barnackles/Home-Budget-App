package com.barnackles.user;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.barnackles.user.User.USERNAME_PATTERN;

@Data
public class UserUpdateDto {

    @Column(unique = true)
    @Length(min = 5, message = "Your user name must have at least 5 characters")
    @Pattern(regexp = USERNAME_PATTERN, message = "Your user name must comprise only of letters, digits and underscore")
    @NotBlank(message = "Please provide a user name")
//    @UniqueUserName
    private String userName;
    @Column(unique = true)
    @Email(message = "Please provide a valid Email")
    @NotBlank(message = "Please provide an email")
//    @UniqueEmail
    private String email;

}
