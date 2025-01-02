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
public class RegisterDto {  //TODO: add/change fields needed to  create strip account:
                            //       firstName, lastName, phoneNumber
                            // TODO: and add additional firlds to user entity
    @NotEmpty
    private String name;
    @Size(min = 3, max = 20, message = "username need's to contain 3-20 characters")
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
