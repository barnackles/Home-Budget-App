package com.barnackles.operation;

import com.barnackles.category.Category;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operations")
public class Operation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private OperationType operationType;
    private BigDecimal amount;
    private LocalDateTime operationDateTime;
    private int actualOrPlanned;
    private int fixedOrVariable;
    private Frequency frequency;


}
