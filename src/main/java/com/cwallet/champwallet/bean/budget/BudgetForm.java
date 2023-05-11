package com.cwallet.champwallet.bean.budget;

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
public class BudgetForm {
    private long id;
    @NotNull
    @Size(min=5, max=64)
    private String name;
    @NotNull
    @Size(min=5, max=2048)
    private String description;
}
