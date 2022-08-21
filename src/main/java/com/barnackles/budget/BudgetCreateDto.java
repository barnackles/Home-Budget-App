package com.barnackles.budget;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class BudgetCreateDto {

    @Length(max = 100)
    private String budgetName;

}
