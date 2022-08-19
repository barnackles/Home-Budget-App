package com.barnackles.validator;


import com.barnackles.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Slf4j
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

//    @Override
//    public void initialize(UniqueEmail constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
//    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        boolean isEmailTaken = userRepository.findUserByEmail(value).isPresent();
        log.info("email: {} is taken: {}", value, isEmailTaken);
        return isEmailTaken;
    }
}
