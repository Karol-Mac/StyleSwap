package com.restapi.styleswap.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotNull
    @Pattern(regexp = "^[0-9]{3}[- ]?[0-9]{3}[- ]?[0-9]{3}",
             message = "enter only phone number (without counytry code)")
    private String phoneNumber;

    @Size(min = 3, max = 50, message = "username need's to contain 3-50 characters")
    @NotNull
    private String username;
    @Email(message = "email has to contain '@'.")
    @NotNull
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "must have at least: 1 small letter, 1 uppercase letter, 1 number, " +
                    "1 special character & at least 8 characters (total) ")
    private String password;
}
