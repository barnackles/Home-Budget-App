package com.barnackles.budget;


import com.barnackles.operation.Operation;
import lombok.Data;

import javax.persistence.Id;
import java.util.List;

@Data
public class BudgetResponseDto {
    @Id
    private Long id;
    private String budgetName;
    private List<Operation> operations;
}
