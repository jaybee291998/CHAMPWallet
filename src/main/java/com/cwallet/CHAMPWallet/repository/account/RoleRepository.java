package com.cwallet.CHAMPWallet.repository.account;

import com.cwallet.CHAMPWallet.models.account.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
