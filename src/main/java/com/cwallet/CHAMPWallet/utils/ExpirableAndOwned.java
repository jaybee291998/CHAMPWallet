package com.cwallet.CHAMPWallet.utils;

import com.cwallet.CHAMPWallet.models.account.Wallet;

import java.time.LocalDateTime;

public interface ExpirableAndOwned {
    LocalDateTime getCreationTime();
    Wallet getOwner();
}
