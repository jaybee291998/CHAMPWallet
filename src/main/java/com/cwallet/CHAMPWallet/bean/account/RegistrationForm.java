package com.cwallet.CHAMPWallet.bean.account;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegistrationForm {
    @NotNull
    @Size(min=5, max=255)
    private String username;
    @NotNull
    @Size(min=5, max=255)
    private String email;
    @NotNull
    @Size(min=5, max=255)
    private String password;
    @NotNull
    @Size(min=5, max=255)
    private String confirmPassword;
}
