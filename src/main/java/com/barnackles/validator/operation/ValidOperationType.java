package com.barnackles.validator.operation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidOperationTypeValidator.class)
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface ValidOperationType {

    String message() default "Invalid operation type. Available operations types are: INCOME, EXPENSE or SAVING.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
