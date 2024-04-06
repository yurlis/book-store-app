package com.bookstoreapp.validator.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordCustomValidator implements
        ConstraintValidator<PasswordConstraint, String> {
    public static final String PASSWORD_REGEXP =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$";
    public static final int MIN_PASSWORD_LENGTH = 8;

    public static final int MAX_PASSWORD_LENGTH = 128;

    @Override
    public void initialize(PasswordConstraint password) {
    }

    @Override
    public boolean isValid(String password,
                           ConstraintValidatorContext cxt) {
        return password != null && password.matches(PASSWORD_REGEXP)
                && (password.length() >= MIN_PASSWORD_LENGTH)
                && (password.length() < MAX_PASSWORD_LENGTH);
    }
}
