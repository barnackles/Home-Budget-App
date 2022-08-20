package com.barnackles.budget.admin;


import com.barnackles.operation.Operation;
import lombok.Data;

import java.util.List;

@Data
public class BudgetAdminResponseDto {

    private Long Id;
    private String budgetName;
    private Long userId;
    private String userName;
    private List<Operation> operations;
}
