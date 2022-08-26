package com.barnackles.user.admin;


import com.barnackles.budget.Budget;
import com.barnackles.role.Role;
import com.barnackles.validator.email.UniqueEmail;
import com.barnackles.validator.username.UniqueUserName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;

import static com.barnackles.user.User.USERNAME_PATTERN;

@Data
public class UserAdminUpdateDto {


    @Column(unique = true)
    @Length(min = 5, max = 50, message = "Username must have at least 5 characters and not more than 50 characters")
    @Pattern(regexp = USERNAME_PATTERN, message = "Username must comprise only of letters, digits and underscore")
    @NotBlank(message = "Please provide a username")
    @UniqueUserName
    private String userName;
    @Length(max = 200)
    @Column(unique = true)
    @Email(message = "Please provide a valid Email")
    @NotBlank(message = "Please provide an email")
    @UniqueEmail
    private String email;
    private Boolean active;
    private Set<Role> roles;
    private List<Budget> budgets;


}
