package com.cwallet.champwallet.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetTransferForm {
    @NotNull
    @Size(min=5, max=2048)
    private String description;
    @NotNull
    private Double amount;
    @NotNull
    private Long recipientBudgetID;
}
