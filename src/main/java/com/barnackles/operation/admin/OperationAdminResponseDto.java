package com.barnackles.operation.admin;

import com.barnackles.category.Category;
import com.barnackles.operation.ActualOrPlanned;
import com.barnackles.operation.OperationFrequency;
import com.barnackles.operation.OperationType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OperationAdminResponseDto {

    private Long id;
    private UUID uuid;
    private String description;
    private Category category;
    private BigDecimal amount;
    private OperationType operationType;
    private LocalDateTime operationDateTime;
    private ActualOrPlanned actualOrPlanned;
    private OperationFrequency frequency;
    private Long budgetId;
    private String budgetName;

}
