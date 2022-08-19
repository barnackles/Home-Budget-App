package com.barnackles.budget;

import com.barnackles.operation.Operation;
import com.barnackles.user.User;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String budgetName;
    @OneToMany
    @JoinColumn(name = "operation_id")
    private List<Operation> operations;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<User> users;

}
