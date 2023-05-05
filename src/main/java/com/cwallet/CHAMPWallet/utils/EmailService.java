package com.cwallet.CHAMPWallet.utils;

import com.sun.mail.util.MailConnectException;

import java.net.ConnectException;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text) throws ConnectException;
}
