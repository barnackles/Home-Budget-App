package com.barnackles.user;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class UserPasswordUpdateDto {

    @Length(min = 8, message = "Your password must have at least 8 characters")
    @NotBlank(message = "Please provide your password")
    private String newPassword;
    private String currentPassword;


}
