package com.cwallet.CHAMPWallet;

import com.cwallet.CHAMPWallet.models.account.Verification;
import com.cwallet.CHAMPWallet.repository.account.VerificationRepository;
import com.cwallet.CHAMPWallet.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class ChampWalletApplication {
	private static EmailService emailSender;
	private static VerificationRepository verificationRepository;
	@Autowired
	public ChampWalletApplication(EmailService emailSender, VerificationRepository verificationRepository){
		this.emailSender = emailSender;
		this.verificationRepository = verificationRepository;
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
		LocalDateTime end = LocalDateTime.now();
		LocalDateTime start = end.minusMinutes(15);

		System.out.println("Start: " + start);
		System.out.print("End: " + end);
		List<Verification> verifications = verificationRepository.findLatestTimestampByAccountID(start, end, 5);
		verifications.stream().forEach(System.out::println);
	}

}
