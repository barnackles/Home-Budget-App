package com.barnackles.validator.operation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidActualOrPlannedValidator.class)
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface ValidActualOrPlanned {

    String message() default "Invalid actual or planned operation type. Operation can only be ACTUAL or PLANNED.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
