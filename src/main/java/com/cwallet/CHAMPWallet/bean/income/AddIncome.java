package com.cwallet.CHAMPWallet.bean.income;
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
public class AddIncome {
    @NotNull
    @Size(min=5, max=255)
    private String source;
    @NotNull
    @Size(min=5, max=2048)
    private String description;
}
