package com.barnackles.operation;

import com.barnackles.validator.operation.ValidActualOrPlanned;
import com.barnackles.validator.operation.ValidCategory;
import com.barnackles.validator.operation.ValidFrequency;
import com.barnackles.validator.operation.ValidOperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OperationCreateDto {

    @Length(max = 200)
    private String description;
    @NotBlank
    @ValidCategory
    private String category;
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
    @NotBlank
    @ValidOperationType
    private String operationType;
    @NotNull
    @DateTimeFormat
    private LocalDateTime operationDateTime;
    @NotNull
    @NotBlank
    @ValidActualOrPlanned
    private String actualOrPlanned;
    @NotBlank
    @ValidFrequency
    private String frequency;

}
