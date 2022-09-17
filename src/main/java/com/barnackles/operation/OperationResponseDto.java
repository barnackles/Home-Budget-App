package com.barnackles.operation;

import com.barnackles.category.CategoryResponseDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OperationResponseDto {

    private UUID uuid;
    private String description;
    private CategoryResponseDto category;
    private BigDecimal amount;
    private OperationType operationType;
    private LocalDateTime operationDateTime;
    private ActualOrPlanned actualOrPlanned;
    private OperationFrequency frequency;
    private String budgetName;


}
