package com.barnackles.validator.uuid;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;


@Slf4j
@AllArgsConstructor
public class ValidUuidStringValidator implements ConstraintValidator<ValidUuidString, String> {
    private final Pattern UUIDREGEX = Pattern.compile("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$");


    @Override
    public void initialize(ValidUuidString constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (UUIDREGEX.matcher(value).matches()) {
            log.info("UUID string: {} is in correct format.", value);
            return true;
        }
        log.info("UUID: {} is incorrect format.", value);
        return false;
    }
}
