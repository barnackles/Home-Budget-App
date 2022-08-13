package com.barnackles.user;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
public class UserPasswordUpdateDto {

    @Id
    private Long id;
    @Length(min = 8, message = "Your password must have at least 8 characters")
    @NotBlank(message = "Please provide your password")
    private String newPassword;
    private String currentPassword;


}
