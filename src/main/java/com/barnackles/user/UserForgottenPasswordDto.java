package com.barnackles.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserForgottenPasswordDto {
    @Email
    @NotBlank
    private String email;
}
