package com.bookstoreapp.dto.user;

import com.bookstoreapp.validator.password.FieldMatch;
import com.bookstoreapp.validator.password.PasswordConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch.List({
        @FieldMatch(first = "password", second = "repeatPassword",
                message = "The password fields must match")
})
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @PasswordConstraint
    private String password;
    @NotBlank
    @Length(min = 8, max = 35)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
