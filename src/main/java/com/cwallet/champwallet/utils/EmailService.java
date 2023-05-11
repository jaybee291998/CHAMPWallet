package com.cwallet.champwallet.utils;

import javax.mail.SendFailedException;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text) throws SendFailedException;
}
