package com.bezkoder.spring.security.postgresql.config;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Getter
@Setter
public class ResetPasswordRequest {

    @Email
    private String email;

    @Size(min = 6, max = 40)
    private String oldPassword;

    @Size(min = 6, max = 40)
    private String newPassword;
}
