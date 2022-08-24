package com.barnackles.budget.admin;


import lombok.Data;

@Data
public class BudgetAdminResponseDtoAll {

    private Long Id;
    private String budgetName;
    private Long userId;
    private String userName;
}
