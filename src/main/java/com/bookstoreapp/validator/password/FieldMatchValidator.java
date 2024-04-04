package com.bookstoreapp.validator.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object firstObj = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        Object secondObj = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);
        if (firstObj != null) {
            return firstObj.equals(secondObj);
        } else {
            return secondObj == null;
        }
    }
}
