package com.cwallet.champwallet.service.account;

import com.cwallet.champwallet.dto.account.VerificationDTO;
import com.cwallet.champwallet.exception.account.AccountAlreadyActivatedException;
import com.cwallet.champwallet.exception.account.NoSuchAccountException;
import com.cwallet.champwallet.exception.account.VerificationAlreadyUsedException;
import com.cwallet.champwallet.models.account.UserEntity;

public interface VerificationService {
    String generateCode();
    void saveVerificationCode(VerificationDTO verificationDTO);
    VerificationDTO requestVerification(UserEntity user);

    boolean validateAccount(String code, long account_id) throws NoSuchAccountException, AccountAlreadyActivatedException, VerificationAlreadyUsedException;
    boolean resetPassword(String code, long account_id, String newPassword) throws NoSuchAccountException, VerificationAlreadyUsedException;
    VerificationDTO getLatestValidVerificationByUser(long account_id);
}
