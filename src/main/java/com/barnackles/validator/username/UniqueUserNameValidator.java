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
        boolean isUserNameNotTaken = userRepository.findUserByUserName(value).isEmpty();
        log.info("email: {} is taken: {}", value, isUserNameNotTaken);
        return isUserNameNotTaken;
    }
}
