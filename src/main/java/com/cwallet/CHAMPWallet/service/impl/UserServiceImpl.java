package com.cwallet.CHAMPWallet.service.impl;

import com.cwallet.CHAMPWallet.dto.UserEntityDTO;
import com.cwallet.CHAMPWallet.dto.VerificationDTO;
import com.cwallet.CHAMPWallet.exception.EmailNotUniqueException;
import com.cwallet.CHAMPWallet.exception.UserNameNotUniqueException;
import com.cwallet.CHAMPWallet.models.Role;
import com.cwallet.CHAMPWallet.models.UserEntity;
import com.cwallet.CHAMPWallet.repository.RoleRepository;
import com.cwallet.CHAMPWallet.repository.UserRepository;
import com.cwallet.CHAMPWallet.service.UserService;
import com.cwallet.CHAMPWallet.service.VerificationService;
import com.cwallet.CHAMPWallet.utils.EmailService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.cwallet.CHAMPWallet.mappers.UserMapper.mapToUser;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private VerificationService verificationService;
    private EmailService emailService;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                           VerificationService verificationService, EmailService emailService){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.emailService = emailService;
    }


    @Override
    public void save(UserEntityDTO userDto) throws UserNameNotUniqueException, EmailNotUniqueException {
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
        userRepository.save(user);
        String code = verificationService.generateCode();
        VerificationDTO verificationDTO = VerificationDTO.builder()
                .verification_code(code)
                .user(user)
                .build();
        InetAddress IP= null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        StringBuilder message = new StringBuilder();
        String username = user.getUsername();
        String verificationCode = verificationDTO.getVerification_code();
        String activationLink = String.format("%s:8080?activation=%s", IP, verificationCode);
        message.append(String.format("Dear %s;\n", username));
        message.append(String.format("We received a request to reset your password for your account.\n"));
        message.append(String.format("If you didnt request this password reset please ignore this email"));
        message.append(String.format("To reset you password please click on this link: \n"));
        message.append(activationLink);
        emailService.sendSimpleMessage(user.getEmail(), "password reset", message.toString());
        System.out.println(message.toString());
        verificationService.saveVerificationCode(verificationDTO);
    }

    @Override
    public void requestPasswordReset(String email) {
        InetAddress IP= null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
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
        emailService.sendSimpleMessage(email, "password reset", message.toString());
        verificationService.saveVerificationCode(verificationDTO);
        System.out.println(message.toString());
    }
}
