package com.cwallet.CHAMPWallet.dto;

import com.cwallet.CHAMPWallet.models.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
