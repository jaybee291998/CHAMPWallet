package com.cwallet.CHAMPWallet.service.impl.account;

import com.cwallet.CHAMPWallet.dto.account.UserEntityDTO;
import com.cwallet.CHAMPWallet.dto.account.VerificationDTO;
import com.cwallet.CHAMPWallet.exception.account.EmailNotSentException;
import com.cwallet.CHAMPWallet.exception.account.EmailNotUniqueException;
import com.cwallet.CHAMPWallet.exception.account.UserNameNotUniqueException;
import com.cwallet.CHAMPWallet.models.account.Role;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.repository.account.RoleRepository;
import com.cwallet.CHAMPWallet.repository.account.UserRepository;
import com.cwallet.CHAMPWallet.repository.account.WalletRepository;
import com.cwallet.CHAMPWallet.service.account.UserService;
import com.cwallet.CHAMPWallet.service.account.VerificationService;
import com.cwallet.CHAMPWallet.utils.EmailService;
import com.sun.mail.util.MailConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.cwallet.CHAMPWallet.mappers.account.UserMapper.mapToUser;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private VerificationService verificationService;
    private EmailService emailService;

    private InetAddress IP= null;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                           VerificationService verificationService, EmailService emailService){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.emailService = emailService;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void save(UserEntityDTO userDto) throws UserNameNotUniqueException, EmailNotUniqueException, EmailNotSentException {
        UserEntity userEntity = userRepository.findByUsername(userDto.getUsername());
        if(userEntity != null) {
            throw new UserNameNotUniqueException("Usrename not nunique");
        }
        userEntity = userRepository.findFirstByEmail(userDto.getEmail());
        if(userEntity != null) {
            throw  new EmailNotUniqueException("Email is already used");
        }
        Role role = roleRepository.findByName("USER");
        userDto.setRoles(Arrays.asList(role));
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserEntity user = mapToUser(userDto);
        UserEntity newUser = userRepository.save(user);
        Wallet newWallet = Wallet.builder()
                .balance(0)
                .user(newUser)
                .build();
        walletRepository.save(newWallet);
        String code = verificationService.generateCode();
        VerificationDTO verificationDTO = VerificationDTO.builder()
                .verification_code(code)
                .user(user)
                .build();

        StringBuilder message = new StringBuilder();
        String username = user.getUsername();
        String verificationCode = verificationDTO.getVerification_code();
        String activationLink = String.format("%s:8080?activation=%s", IP, verificationCode);
        message.append(String.format("Dear %s;\n", username));
        message.append(String.format("We received a request to reset your password for your account.\n"));
        message.append(String.format("If you didnt request this password reset please ignore this email"));
        message.append(String.format("To reset you password please click on this link: \n"));
        message.append(activationLink);
        try {
            emailService.sendSimpleMessage(user.getEmail(), "password reset", message.toString());
        } catch(ConnectException e){
            e.printStackTrace();
            throw new EmailNotSentException("Email not sent");
        }

        System.out.println(message.toString());
        verificationService.saveVerificationCode(verificationDTO);
    }

    @Override
    public void requestPasswordReset(String email) {
        UserEntity user = userRepository.findByEmail(email);
        VerificationDTO verificationDTO = verificationService.requestVerification(user);
        StringBuilder message = new StringBuilder();
        String username = user.getUsername();
        String verificationCode = verificationDTO.getVerification_code();
        String activationLink = String.format("%s:8080?activation=%s", IP, verificationCode);
        message.append(String.format("Dear %s;\n", username));
        message.append(String.format("We received a request to reset your password for your account.\n"));
        message.append(String.format("If you didnt request this password reset please ignore this email"));
        message.append(String.format("To reset you password please click on this link: \n"));
        message.append(activationLink);
        try {
            emailService.sendSimpleMessage(email, "password reset", message.toString());
        } catch (ConnectException e) {
            throw new RuntimeException(e);
        }
        verificationService.saveVerificationCode(verificationDTO);
        System.out.println(message.toString());
    }
}
