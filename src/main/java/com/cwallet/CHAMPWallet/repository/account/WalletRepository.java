package com.cwallet.CHAMPWallet.repository.account;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
