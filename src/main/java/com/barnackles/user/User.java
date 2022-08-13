package com.barnackles.user;

import com.barnackles.asset.Asset;
import com.barnackles.budget.Budget;
import com.barnackles.role.Role;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    public static final String USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9_]{4,29}$";
    @Id
    @EqualsAndHashCode.Include()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
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
    private Boolean active;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    @ManyToMany
    @JoinTable(name = "user_budget", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "budget_id"))
    private List<Budget> budgets;
    @OneToMany
    @JoinColumn(name = "asset_id")
    private List<Asset> assets;

}

