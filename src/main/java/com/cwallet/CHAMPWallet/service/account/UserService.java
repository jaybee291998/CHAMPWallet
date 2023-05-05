package com.cwallet.CHAMPWallet.service.account;

import com.cwallet.CHAMPWallet.dto.account.UserEntityDTO;
import com.cwallet.CHAMPWallet.exception.account.EmailNotSentException;
import com.cwallet.CHAMPWallet.exception.account.EmailNotUniqueException;
import com.cwallet.CHAMPWallet.exception.account.UserNameNotUniqueException;

public interface UserService {
    void save(UserEntityDTO user) throws UserNameNotUniqueException, EmailNotUniqueException, EmailNotSentException;

    void requestPasswordReset(String email);
}
