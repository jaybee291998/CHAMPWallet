package com.cwallet.CHAMPWallet.service.account;

import com.cwallet.CHAMPWallet.dto.account.VerificationDTO;
import com.cwallet.CHAMPWallet.exception.account.AccountAlreadyActivatedException;
import com.cwallet.CHAMPWallet.exception.account.NoSuchAccountException;
import com.cwallet.CHAMPWallet.exception.account.VerificationAlreadyUsedException;
import com.cwallet.CHAMPWallet.models.account.UserEntity;

public interface VerificationService {
    String generateCode();
    void saveVerificationCode(VerificationDTO verificationDTO);
    VerificationDTO requestVerification(UserEntity user);

    boolean validateAccount(String code, long account_id) throws NoSuchAccountException, AccountAlreadyActivatedException, VerificationAlreadyUsedException;
    boolean resetPassword(String code, long account_id, String newPassword) throws NoSuchAccountException, VerificationAlreadyUsedException;
    VerificationDTO getLatestValidVerificationByUser(long account_id);
}
