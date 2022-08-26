package com.barnackles.budget.admin;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Id;

@Data
public class BudgetAdminUpdateDto {
    @Id
    private Long id;
    @Length(max = 100)
    private String newBudgetName;

}
