package com.cwallet.CHAMPWallet.bean.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetForm {
    @NotNull
    @Size(min=5, max=255)
    private String password;
    @NotNull
    @Size(min=5, max=255)
    private String confirmPassword;
    private String activationCode;
    private long accountID;
}
