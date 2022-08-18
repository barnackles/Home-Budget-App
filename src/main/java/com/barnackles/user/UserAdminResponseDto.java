package com.barnackles.user;


import com.barnackles.asset.Asset;
import com.barnackles.budget.Budget;
import com.barnackles.role.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserAdminResponseDto {

    private Long id;
    private String userName;
    private String email;
    private Boolean active;
    private Set<Role> roles;
    private List<Budget> budgets;
    private List<Asset> assets;




}
