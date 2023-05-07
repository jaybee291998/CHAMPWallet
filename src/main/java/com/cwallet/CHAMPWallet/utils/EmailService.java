package com.cwallet.CHAMPWallet.utils;

import com.sun.mail.util.MailConnectException;

import javax.mail.SendFailedException;
import java.net.ConnectException;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text) throws SendFailedException;
}
