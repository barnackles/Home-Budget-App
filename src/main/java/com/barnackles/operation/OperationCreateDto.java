package com.barnackles.operation;

import com.barnackles.category.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OperationCreateDto {
    // validate enums
    // validate category

    private String description;
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

}
