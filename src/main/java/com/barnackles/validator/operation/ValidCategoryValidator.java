package com.barnackles.validator.operation;


import com.barnackles.category.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Slf4j
@AllArgsConstructor
public class ValidCategoryValidator implements ConstraintValidator<ValidCategory, String> {

    private final CategoryService categoryService;

    @Override
    public void initialize(ValidCategory constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (categoryService.existsByName(value.toLowerCase())) {
            log.info("category: {} exists.", value);
            return true;
        }
        log.info("category: {} does not exist.", value);
        return false;
    }
}
