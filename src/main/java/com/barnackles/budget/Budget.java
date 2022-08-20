package com.barnackles.budget;

import com.barnackles.operation.Operation;
import com.barnackles.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "budgets")
@EqualsAndHashCode
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include()
    private Long id;
    @Length(max = 100)
    private String budgetName;
    @OneToMany
    @JoinColumn(name = "operation_id")
    private List<Operation> operations;
    @ManyToOne
    private User user;

}
