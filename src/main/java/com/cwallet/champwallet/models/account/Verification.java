package com.cwallet.champwallet.models.account;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name="verification")
@ToString(exclude = "user")
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    @JoinColumn(name="account_id", nullable = false)
    private UserEntity user;
    private String verification_code;
    @CreationTimestamp
    private LocalDateTime timestamp;
}
