package com.barnackles.budget;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class BudgetUpdateDto {

    @Length(max = 100)
    private String currentBudgetName;
    @Length(max = 100)
    private String newBudgetName;

}
