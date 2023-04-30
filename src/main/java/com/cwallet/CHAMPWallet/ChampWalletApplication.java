package com.cwallet.CHAMPWallet;

import com.cwallet.CHAMPWallet.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChampWalletApplication {
	private static EmailService emailSender;
	@Autowired
	public ChampWalletApplication(EmailService emailSender){
		this.emailSender = emailSender;
	}

	public static void main(String[] args) {
		SpringApplication.run(ChampWalletApplication.class, args);
		emailSender.sendSimpleMessage("jaybee291998@gmail.com", "test", "this is a test");
		System.out.println("Email Sent");
	}

}
