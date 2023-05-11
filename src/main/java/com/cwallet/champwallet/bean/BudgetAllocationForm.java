package com.cwallet.champwallet.bean;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetAllocationForm {
    @NotNull
    @Size(min=5, max=2048)
    private String description;
    @NotNull
    private double amount;
    @NotNull
    private String type;
}
