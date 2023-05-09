package com.cwallet.CHAMPWallet.utils;

public interface ExpirableAndOwnedService {
    boolean isExpired(ExpirableAndOwned obj);
    boolean isOwnedByLoggedInUser(ExpirableAndOwned obj);
}
