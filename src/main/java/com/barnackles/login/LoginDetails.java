package com.barnackles.login;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDetails {
    @NotBlank(message = "Login cannot be empty.")
    private String username;
    @Length(min = 8, message = "Your password must have at least 8 characters")
    @NotBlank(message = "Password cannot be empty.")
    private String password;

}
