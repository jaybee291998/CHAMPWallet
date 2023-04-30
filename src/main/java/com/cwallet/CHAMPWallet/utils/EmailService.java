package com.cwallet.CHAMPWallet.utils;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}
