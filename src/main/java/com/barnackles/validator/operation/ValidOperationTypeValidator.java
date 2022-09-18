package com.barnackles.validator.operation;


import com.barnackles.category.CategoryService;
import com.barnackles.operation.OperationType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Slf4j
@AllArgsConstructor
public class ValidOperationTypeValidator implements ConstraintValidator<ValidOperationType, String> {

    private final CategoryService categoryService;

    @Override
    public void initialize(ValidOperationType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (OperationType.INCOME.toString().equals(value.toUpperCase())
            || OperationType.EXPENSE.toString().equals(value.toUpperCase())
            || OperationType.SAVING.toString().equals(value.toUpperCase())
        ) {
            log.info("Operation type: {} exists.", value);
            return true;
        }
        log.info("Operation type: {} does not exist.", value);
        return false;
    }
}
