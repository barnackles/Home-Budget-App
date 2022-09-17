package com.barnackles.budget;


import com.barnackles.operation.OperationResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class BudgetResponseDto {

    private String budgetName;
    private List<OperationResponseDto> recentOperations;
}
