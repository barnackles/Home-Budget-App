package com.barnackles.user.admin;


import com.barnackles.role.Role;
import lombok.Data;

import java.util.HashMap;
import java.util.Set;

@Data
public class UserAdminResponseDto {

    private Long id;
    private String userName;
    private String email;
    private Boolean active;
    private Set<Role> roles;
    private HashMap<Long, String> budgets;


}
