package com.barnackles.budget;


import com.barnackles.operation.Operation;
import lombok.Data;

import java.util.List;

@Data
public class BudgetResponseDto {

    private String budgetName;
    private List<Operation> operations;
}
