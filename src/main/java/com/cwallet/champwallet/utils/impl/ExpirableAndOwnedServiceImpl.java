package com.cwallet.champwallet.utils.impl;

import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.utils.ExpirableAndOwned;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class ExpirableAndOwnedServiceImpl implements ExpirableAndOwnedService {
    private SecurityUtil securityUtil;
    @Autowired
    public ExpirableAndOwnedServiceImpl(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }
    @Override
    public boolean isExpired(ExpirableAndOwned obj) {
        if(obj == null) {
            throw new IllegalArgumentException("cant be null");
        }
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(24);
        return !(obj.getCreationTime().isAfter(start) && obj.getCreationTime().isBefore(end));
    }

    @Override
    public boolean isOwnedByLoggedInUser(ExpirableAndOwned obj) {
        if(obj == null) {
            throw new IllegalArgumentException("cant be null");
        }
        return obj.getOwner().equals(securityUtil.getLoggedInUser().getWallet());
    }
}
