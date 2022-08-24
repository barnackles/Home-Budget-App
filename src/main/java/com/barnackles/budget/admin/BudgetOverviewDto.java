package com.barnackles.budget.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetOverviewDto {

    private String userName;
    private String budgetName;
    private BigDecimal budgetBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalSavings;

}
