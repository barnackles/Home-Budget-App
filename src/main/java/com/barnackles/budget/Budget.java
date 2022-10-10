package com.barnackles.budget;

import com.barnackles.operation.Operation;
import com.barnackles.user.User;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(hidden = true)
    private Long id;
    @Length(max = 100)
    @ApiModelProperty(hidden = true)
    private String budgetName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "budget", fetch = FetchType.LAZY)
    @ApiModelProperty(hidden = true)
    private List<Operation> operations;
    @ManyToOne
    @ApiModelProperty(hidden = true)
    private User user;

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", budgetName='" + budgetName + '\'' +
                ", user=" + user +
                '}';
    }
}
