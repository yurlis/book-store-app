package com.bookstoreapp.validator.isbn;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsbnCustomValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsbnConstraint {
    String message() default "Invalid format isbn";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
