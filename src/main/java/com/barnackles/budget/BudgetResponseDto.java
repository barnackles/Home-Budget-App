package com.barnackles.budget;


import com.barnackles.operation.Operation;
import com.barnackles.user.User;

import javax.persistence.*;
import java.util.List;

public class BudgetResponseDto {
    @Id
    private Long id;
    private String budgetName;
    private List<Operation> operations;
    private List<User> users;
}
