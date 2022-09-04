package com.barnackles.validator.username;


import com.barnackles.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Slf4j
@AllArgsConstructor
public class UniqueUserNameValidator implements ConstraintValidator<UniqueUserName, String> {

    private final UserRepository userRepository;


    @Override
    public void initialize(UniqueUserName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (userRepository.findUserByUserName(value).isEmpty()) {
            log.info("username: {} is available.", value);
            return true;
        }
        log.info("username: {} is not available.", value);
        return false;
    }
}
