package com.cwallet.champwallet.bean.expenseType;

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
    private long id;
    @NotNull
    @Size(min=5, max=255)
    private String name;

    @NotNull
    @Size(min=5, max=255)
    private String description;
}
