package com.cwallet.CHAMPWallet.security;

import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.repository.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtil {
    private UserRepository userRepository;
    @Autowired
    public SecurityUtil(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserEntity getLoggedInUser() {
        return userRepository.findFirstByUsername(getSessionUser());
    }

    public static String getSessionUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUsername = authentication.getName().trim();
            return currentUsername;
        }
        return null;
    }
}
