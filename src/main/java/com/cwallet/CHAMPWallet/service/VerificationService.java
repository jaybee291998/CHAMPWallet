package com.cwallet.CHAMPWallet.service;

import com.cwallet.CHAMPWallet.dto.UserEntityDTO;
import com.cwallet.CHAMPWallet.dto.VerificationDTO;
import com.cwallet.CHAMPWallet.models.UserEntity;
import com.cwallet.CHAMPWallet.models.Verification;

public interface VerificationService {
    String generateCode();
    void saveVerificationCode(VerificationDTO verificationDTO);
    VerificationDTO requestVerification(UserEntity user);
}
