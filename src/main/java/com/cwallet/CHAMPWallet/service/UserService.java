package com.cwallet.CHAMPWallet.service;

import com.cwallet.CHAMPWallet.dto.UserEntityDTO;
import com.cwallet.CHAMPWallet.exception.EmailNotUniqueException;
import com.cwallet.CHAMPWallet.exception.UserNameNotUniqueException;
import com.cwallet.CHAMPWallet.models.UserEntity;

public interface UserService {
    void save(UserEntityDTO user) throws UserNameNotUniqueException, EmailNotUniqueException;

    void requestPasswordReset(String email);
}
