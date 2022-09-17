package com.barnackles.validator.operation;


import com.barnackles.category.CategoryService;
import com.barnackles.operation.ActualOrPlanned;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Slf4j
@AllArgsConstructor
public class ValidActualOrPlannedValidator implements ConstraintValidator<ValidActualOrPlanned, String> {

    private final CategoryService categoryService;

    @Override
    public void initialize(ValidActualOrPlanned constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (ActualOrPlanned.ACTUAL.toString().equals(value.toUpperCase())
            || ActualOrPlanned.PLANNED.toString().equals(value.toUpperCase())) {
            log.info("Operation type: {} exists.", value);
            return true;
        }
        log.info("Operation type: {} does not exist.", value);
        return false;
    }
}
