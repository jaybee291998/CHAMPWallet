package com.cwallet.CHAMPWallet;

import com.cwallet.CHAMPWallet.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class ChampWalletApplication {
	private static EmailService emailSender;
	@Autowired
	public ChampWalletApplication(EmailService emailSender){
		this.emailSender = emailSender;
	}

	public static void main(String[] args) {
		SpringApplication.run(ChampWalletApplication.class, args);
		/*InetAddress IP= null;
		try {
			IP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		System.out.println("IP of my system is := "+IP.getHostAddress());
		emailSender.sendSimpleMessage("jaybee291998@gmail.com", "test"+IP.getHostAddress(), "this is a test");
		System.out.println("Email Sent");*/
	}

}
