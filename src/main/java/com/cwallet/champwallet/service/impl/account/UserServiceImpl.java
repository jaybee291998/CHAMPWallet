package com.cwallet.champwallet.service.impl.account;

import com.cwallet.champwallet.dto.account.UserEntityDTO;
import com.cwallet.champwallet.dto.account.VerificationDTO;
import com.cwallet.champwallet.exception.account.*;
import com.cwallet.champwallet.models.account.Role;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.repository.account.RoleRepository;
import com.cwallet.champwallet.repository.account.UserRepository;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.service.account.UserService;
import com.cwallet.champwallet.service.account.VerificationService;
import com.cwallet.champwallet.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.SendFailedException;
import javax.transaction.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.cwallet.champwallet.mappers.account.UserMapper.mapToUser;

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

    @Transactional
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
        String message = getActivationMessage(user.getUsername(), String.valueOf(user.getId()), verificationDTO.getVerification_code(), IP.getHostAddress());

        try {
            emailService.sendMIMEMessage(user.getEmail(), "Account Activation", message);
        } catch(SendFailedException e){
            e.printStackTrace();
            throw new EmailNotSentException("Email not sent");
        }
        verificationService.saveVerificationCode(verificationDTO);
    }

    @Override
    public void requestPasswordReset(String email) throws NoSuchAccountException, SendFailedException {
        if(email == null || email.equals("")) throw new IllegalArgumentException("Email must not be null or empty");
        UserEntity user = userRepository.findByEmail(email);
        if(user == null) throw new NoSuchAccountException(String.format("Account with email: %s doesnt exsit", email));

        VerificationDTO verificationDTO = verificationService.requestVerification(user);
        String username = user.getUsername();
        String verificationCode = verificationDTO.getVerification_code();
        String message = getPasswordResetMessage(username, String.valueOf(user.getId()), verificationCode, IP.getHostAddress());

        try {
            emailService.sendMIMEMessage(email, "Password Reset", message);
            verificationService.saveVerificationCode(verificationDTO);
        } catch (SendFailedException e) {
            throw new SendFailedException("Failed to send email");
        }
    }

    @Override
    public void requestVerificationCode(String email) throws NoSuchAccountException, AccountAlreadyActivatedException, EmailNotSentException {
        if(email == null || email.equals("")) throw new IllegalArgumentException("Email must not be null or empty");
        UserEntity user = userRepository.findByEmail(email);
        if(user == null) throw new NoSuchAccountException(String.format("Account with email: %s doesnt exsit", email));
        if(user.isActive()) throw new AccountAlreadyActivatedException("Account already activated");

        VerificationDTO verificationDTO = verificationService.requestVerification(user);
        String username = user.getUsername();
        String verificationCode = verificationDTO.getVerification_code();
        String message = getActivationMessage(username, String.valueOf(user.getId()), verificationCode, IP.getHostAddress());

        try {
            emailService.sendMIMEMessage(email, "Account Activation", message);
        } catch (SendFailedException e) {
            throw new EmailNotSentException("Email not sent");
        }
        verificationService.saveVerificationCode(verificationDTO);
    }

    private static String getActivationMessage(String username, String accountID, String verificationCode, String ipAddress) {
        StringBuilder message = new StringBuilder();
        String activationLink = String.format("<a href=\"%s:8080/activate-account?activation=%s&account=%s\">Click to verify account</a>", ipAddress, verificationCode, accountID);
        message.append(String.format("<h2>Dear %s;</h2>", username));
        message.append("<p>You have successfully registered to CHAMP Wallet.</p>");
        message.append("<p>Please verify your account by following the instruction.</p>");
        message.append("<p>To verify your account please click on this link: </p>");
        message.append(activationLink);
        return  message.toString();
    }

    private static String getPasswordResetMessage(String username, String accountID, String verificationCode, String ipAddress) {
        StringBuilder message = new StringBuilder();
        String activationLink = String.format("<a href=\"%s:8080/reset-password?activation=%s&account=%s\">Click here to reset password</a>", ipAddress, verificationCode, accountID);
        message.append(String.format("<h2>Dear %s;</h2>", username));
        message.append("<p>We received a request to reset your password for your account.</p>");
        message.append("<p>If you didn't request this password reset please ignore this email.</p>");
        message.append("<p>To reset you password please click on this link: </p>");
        message.append(activationLink);
        return  message.toString();
    }
}
