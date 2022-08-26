package com.barnackles.operation;

import com.barnackles.budget.Budget;
import com.barnackles.category.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.uuid.Generators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "operations")
@EqualsAndHashCode
public class Operation {

    // validate enums
    // validate category
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include()
    private Long id;
    @Column(name = "operation_number", unique = true, columnDefinition = "BINARY(16)")
    private UUID uuid;
    @Length(max = 200)
    private String description;
    @ManyToOne
    @NotNull
    private Category category;
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
    @NotNull
    private OperationType operationType;
    @NotNull

    private LocalDateTime operationDateTime;
    @NotNull
    private ActualOrPlanned actualOrPlanned;
    @NotNull
    private OperationFrequency frequency;
    @ManyToOne
    @JsonIgnore
    private Budget budget;


    @PrePersist
    private void prePersist() {
        //check for collisions
        uuid = Generators.timeBasedGenerator().generate();
    }


}
