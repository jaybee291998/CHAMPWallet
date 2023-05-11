package com.cwallet.champwallet.repository.account;

import com.cwallet.champwallet.models.account.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
