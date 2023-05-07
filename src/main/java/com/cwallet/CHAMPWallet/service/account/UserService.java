package com.cwallet.CHAMPWallet.service.account;

import com.cwallet.CHAMPWallet.dto.account.UserEntityDTO;
import com.cwallet.CHAMPWallet.exception.account.*;

import javax.mail.SendFailedException;

public interface UserService {
    void save(UserEntityDTO user) throws UserNameNotUniqueException, EmailNotUniqueException, EmailNotSentException;

    void requestPasswordReset(String email) throws NoSuchAccountException, SendFailedException;
    void requestVerificationCode(String email) throws NoSuchAccountException, AccountAlreadyActivatedException, EmailNotSentException;
}
