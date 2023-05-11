package com.cwallet.champwallet.repository.account;

import com.cwallet.champwallet.models.account.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);

    UserEntity findFirstByUsername(String username);

    UserEntity findFirstByEmail(String email);

}
