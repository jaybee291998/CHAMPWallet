package com.cwallet.champwallet.utils;

public interface ExpirableAndOwnedService {
    boolean isExpired(ExpirableAndOwned obj);
    boolean isOwnedByLoggedInUser(ExpirableAndOwned obj);
}
