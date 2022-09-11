package com.barnackles.validator.uuid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidUuidStringValidator.class)
@Retention(RUNTIME)
@Target({ElementType.PARAMETER, FIELD, METHOD})
public @interface ValidUuidString {

    String message() default "Invalid token format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
