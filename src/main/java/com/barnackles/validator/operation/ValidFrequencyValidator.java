package com.barnackles.validator.operation;


import com.barnackles.category.CategoryService;
import com.barnackles.operation.OperationFrequency;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Slf4j
@AllArgsConstructor
public class ValidFrequencyValidator implements ConstraintValidator<ValidFrequency, String> {

    private final CategoryService categoryService;

    @Override
    public void initialize(ValidFrequency constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (OperationFrequency.NONE.toString().equals(value.toUpperCase())
            || OperationFrequency.DAILY.toString().equals(value.toUpperCase())
            || OperationFrequency.WEEKLY.toString().equals(value.toUpperCase())
            || OperationFrequency.MONTHLY.toString().equals(value.toUpperCase())
            || OperationFrequency.QUARTERLY.toString().equals(value.toUpperCase())
            || OperationFrequency.ANNUALLY.toString().equals(value.toUpperCase())
        ) {
            log.info("Operation frequency: {} exists.", value);
            return true;
        }
        log.info("Operation frequency: {} does not exist.", value);
        return false;
    }
}
