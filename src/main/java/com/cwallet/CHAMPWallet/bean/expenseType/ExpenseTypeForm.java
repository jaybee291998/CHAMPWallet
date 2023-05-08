package com.cwallet.CHAMPWallet.bean.expenseType;

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
public class ExpenseTypeForm {
    @NotNull
    @Size(min=5, max=255)
    private String name;

    @NotNull
    @Size(min=5, max=255)
    private String description;
}
