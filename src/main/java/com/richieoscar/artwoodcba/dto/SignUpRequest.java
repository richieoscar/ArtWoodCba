package com.richieoscar.artwoodcba.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(@NotBlank String password, @NotBlank  String confirmPassword, @NotBlank @Email String email, @NotBlank String phone, @NotBlank String firstName,
                            @NotBlank String lastName) {

}
