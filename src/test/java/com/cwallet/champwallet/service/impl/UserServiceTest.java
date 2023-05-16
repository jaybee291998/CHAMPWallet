package com.cwallet.champwallet.service.impl;

import com.cwallet.champwallet.dto.account.UserEntityDTO;
import com.cwallet.champwallet.dto.account.VerificationDTO;
import com.cwallet.champwallet.exception.account.*;
import com.cwallet.champwallet.models.account.Role;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Verification;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.repository.account.RoleRepository;
import com.cwallet.champwallet.repository.account.UserRepository;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.service.account.UserService;
import com.cwallet.champwallet.service.account.VerificationService;
import com.cwallet.champwallet.service.impl.account.UserServiceImpl;
import com.cwallet.champwallet.utils.EmailService;
import com.sun.nio.sctp.IllegalReceiveException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.SendFailedException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.cwallet.champwallet.mappers.account.UserMapper.mapToUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerificationService verificationService;
    @Mock
    private EmailService emailService;
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test(expected = UserNameNotUniqueException.class)
    public void testSave_UsernameAlreadyUsedShouldThrowUserNameNotUniqueException() throws EmailNotSentException, UserNameNotUniqueException, EmailNotUniqueException {
        String username = "testUsername";
        UserEntityDTO userEntityDTO = UserEntityDTO.builder()
                .username(username)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(new UserEntity());
        userService.save(userEntityDTO);
    }
    @Test(expected = EmailNotUniqueException.class)
    public void testSave_EmailAlreadyTakenShouldThrowEmailNotUniqueException() throws EmailNotSentException, UserNameNotUniqueException, EmailNotUniqueException {
        String email = "test@email.com";
        String username = "testUsername";
        UserEntityDTO userEntityDTO = UserEntityDTO.builder()
                .email(email)
                .build();
//        when(userRepository.findByUsername(username)).thenReturn(null);
        when(userRepository.findFirstByEmail(email)).thenReturn(new UserEntity());
        userService.save(userEntityDTO);
    }
    @Test
    public void testSaveShouldSucceed() throws EmailNotSentException, UserNameNotUniqueException, EmailNotUniqueException, SendFailedException {
        String username = "test";
        String email = "test@email.com";
        String password = "testPassword";
        String encryptedPassword = "encrypted";
        Role role = Role.builder()
                .id(1L)
                .name("USER")
                .users(new ArrayList<>())
                .build();
        UserEntityDTO userDTO = UserEntityDTO.builder()
                .username(username)
                .password(password)
                .email(email)
                .roles(Arrays.asList(role))
                .build();
        when(userRepository.findByUsername(username)).thenReturn(null);
        when(userRepository.findFirstByEmail(email)).thenReturn(null);
        when(roleRepository.findByName("USER")).thenReturn(role);
        when(passwordEncoder.encode(password)).thenReturn(encryptedPassword);
        UserEntity user = mapToUser(userDTO);
        UserEntity newUser = UserEntity.builder()
                .id(1L)
                .username(username)
                .password(encryptedPassword)
                .email(email)
                .roles(Arrays.asList(role))
                .build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);
        Wallet newWallet = Wallet.builder()
                .balance(0)
                .user(newUser)
                .build();
        when(walletRepository.save(any(Wallet.class))).thenReturn(new Wallet());
        String code = "123456";
        when(verificationService.generateCode()).thenReturn(code);
        userService.save(userDTO);

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).findByUsername(username);
        verify(emailService, times(1)).sendMIMEMessage(anyString(), anyString(), anyString());
        verify(verificationService, times(1)).saveVerificationCode(any(VerificationDTO.class));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequestPasswordReset_ShouldThrowIllegalArgumentException_WhenEmailIsEmpty() throws NoSuchAccountException, SendFailedException {
        String email = "";
        userService.requestPasswordReset(email);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testRequestPasswordReset_ShouldThrowIllegalArgumentException_WhenEmailIsNull() throws NoSuchAccountException, EmailNotSentException, AccountAlreadyActivatedException {
        String email = null;
        userService.requestVerificationCode(email);
    }
    @Test(expected = NoSuchAccountException.class)
    public void testRequestPasswordReset_ShouldThrowNoSuchAccountException_WhenEmailNotRegistered() throws NoSuchAccountException, EmailNotSentException, AccountAlreadyActivatedException {
        String notRegisteredEmail = "test@email.com";
        when(userRepository.findByEmail(notRegisteredEmail)).thenReturn(null);
        userService.requestVerificationCode(notRegisteredEmail);
        verify(userRepository, times(1)).findByEmail(notRegisteredEmail);
    }

    @Test
    public void testRequestPasswordReset_ShouldSucceed() throws NoSuchAccountException, EmailNotSentException, AccountAlreadyActivatedException {
        String code = "123456";
        String registeredEmail = "test@email.com";
        UserEntity user = new UserEntity();
        user.setEmail(registeredEmail);
        VerificationDTO verificationDTO = VerificationDTO.builder()
                .verification_code(code)
                .user(user)
                .build();
        when(userRepository.findByEmail(registeredEmail)).thenReturn(user);
        when(verificationService.requestVerification(user)).thenReturn(verificationDTO);

        userService.requestVerificationCode(registeredEmail);

        verify(userRepository, times(1)).findByEmail(registeredEmail);
        verify(verificationService, times(1)).requestVerification(user);
        verify(verificationService, times(1)).saveVerificationCode(verificationDTO);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testRequestVerificationCode_ShouldThrowIllegalArgument_WhenEmailIsEmpty() throws NoSuchAccountException, EmailNotSentException, AccountAlreadyActivatedException {
        String email = "";
        userService.requestVerificationCode(email);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testRequestVerificationCode_ShouldThrowIllegalArgument_WhenEmailIsNull() throws NoSuchAccountException, EmailNotSentException, AccountAlreadyActivatedException {
        String nullEmail = null;
        userService.requestVerificationCode(nullEmail);
    }
    @Test(expected = NoSuchAccountException.class)
    public void testRequestVerificationCode_ShouldThrowNoSuchAccountException_WhenEmailIsNotRegistered() throws NoSuchAccountException, EmailNotSentException, AccountAlreadyActivatedException {
        String notRegisteredEmail = "test@email.com";
        when(userRepository.findByEmail(notRegisteredEmail)).thenReturn(null);
        userService.requestVerificationCode(notRegisteredEmail);

        verify(userRepository, times(1)).findByEmail(notRegisteredEmail);
    }
    @Test(expected = AccountAlreadyActivatedException.class)
    public void testRequestVerificationCode_ShouldThrowAccountAlreadyActivatedException_WhenUserIsAlreadyActivated() throws NoSuchAccountException, EmailNotSentException, AccountAlreadyActivatedException {
        String registeredEmail = "test@email.com";
        boolean isActive = true;
        UserEntity user = UserEntity.builder()
                .email(registeredEmail)
                .isActive(isActive)
                .build();
        when(userRepository.findByEmail(registeredEmail)).thenReturn(user);

        userService.requestVerificationCode(registeredEmail);

        verify(userRepository, times(1)).findByEmail(registeredEmail);
    }
    @Test
    public void testRequestVerificationCode_ShouldSucceed() throws NoSuchAccountException, EmailNotSentException, AccountAlreadyActivatedException, SendFailedException {
        String registeredEmail = "test@email.com";
        boolean isActive = false;
        String code = "123456";
        UserEntity user = UserEntity.builder()
                .email(registeredEmail)
                .isActive(isActive)
                .build();
        VerificationDTO verificationDTO = VerificationDTO.builder()
                .verification_code(code)
                .user(user)
                .build();
        when(userRepository.findByEmail(registeredEmail)).thenReturn(user);
        when(verificationService.requestVerification(user)).thenReturn(verificationDTO);

        userService.requestVerificationCode(registeredEmail);

        verify(userRepository, times(1)).findByEmail(registeredEmail);
        verify(verificationService, times(1)).requestVerification(user);
        verify(emailService, times(1)).sendMIMEMessage(anyString(), anyString(), anyString());
        verify(verificationService, times(1)).saveVerificationCode(verificationDTO);
    }
}
