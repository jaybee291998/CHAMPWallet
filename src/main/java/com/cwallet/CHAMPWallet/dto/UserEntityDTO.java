package com.cwallet.CHAMPWallet.dto;

import com.cwallet.CHAMPWallet.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntityDTO {
    private long id;
    private String username;
    private String email;
    private String password;
    private boolean isActive = false;
    private List<Role> roles;
}
