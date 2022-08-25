package com.barnackles.budget.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetOverviewDto {

    private String userName;
    private String budgetName;
    private BigDecimal balance;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal savings;

}
