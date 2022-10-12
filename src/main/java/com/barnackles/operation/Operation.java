package com.barnackles.operation;

import com.barnackles.budget.Budget;
import com.barnackles.category.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.uuid.Generators;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(hidden = true)
    private Long id;
    @Column(name = "operation_number", unique = true, columnDefinition = "BINARY(16)")
    @ApiModelProperty(hidden = true)
    private UUID uuid;
    @Length(max = 200)
    @ApiModelProperty(hidden = true)
    private String description;
    @ManyToOne
    @NotNull
    @ApiModelProperty(hidden = true)
    private Category category;
    @DecimalMin(value = "0.01")
    @ApiModelProperty(hidden = true)
    private BigDecimal amount;
    @NotNull
    @ApiModelProperty(hidden = true)
    private OperationType operationType;
    @NotNull
    @ApiModelProperty(hidden = true)
    private LocalDateTime operationDateTime;
    @NotNull
    @ApiModelProperty(hidden = true)
    private ActualOrPlanned actualOrPlanned;
    @NotNull
    @ApiModelProperty(hidden = true)
    private OperationFrequency frequency;
    @ManyToOne
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Budget budget;


    @PrePersist
    private void prePersist() {
        //check for collisions
        uuid = Generators.timeBasedGenerator().generate();
    }


}
