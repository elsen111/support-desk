package com.supportdesk.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User registration request")
public class RegisterRequest {

    @NotBlank(message = "Username is required.")
    @Size(max = 50, message = "Username cannot exceed 50 characters.")
    @Schema(example = "john222")
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    @Schema(example = "john@gmail.com")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Schema(example = "StrongPassword!!")
    private String password;

}
