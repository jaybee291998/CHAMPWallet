package com.cwallet.CHAMPWallet.service.account;

import com.cwallet.CHAMPWallet.dto.account.VerificationDTO;
import com.cwallet.CHAMPWallet.models.account.UserEntity;

public interface VerificationService {
    String generateCode();
    void saveVerificationCode(VerificationDTO verificationDTO);
    VerificationDTO requestVerification(UserEntity user);
}
