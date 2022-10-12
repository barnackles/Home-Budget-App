package com.barnackles.validator.operation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidCategoryValidator.class)
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface ValidCategory {

    String message() default "Category does not exist.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
