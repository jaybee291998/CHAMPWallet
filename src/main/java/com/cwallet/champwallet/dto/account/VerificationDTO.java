package com.cwallet.champwallet.dto.account;

import com.cwallet.champwallet.models.account.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationDTO {
    private long id;
    @ToString.Exclude
    private UserEntity user;
    private String verification_code;
    private LocalDateTime timestamp;
}
