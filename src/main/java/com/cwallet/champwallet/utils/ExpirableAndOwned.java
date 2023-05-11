package com.cwallet.champwallet.utils;

import com.cwallet.champwallet.models.account.Wallet;

import java.time.LocalDateTime;

public interface ExpirableAndOwned {
    LocalDateTime getCreationTime();
    Wallet getOwner();
}
