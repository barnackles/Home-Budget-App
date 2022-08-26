package com.barnackles.validator.email;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UniqueEmailValidator.class)
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface UniqueEmail {

    String message() default "Forgot your account’s password? Go to password recovery service.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
