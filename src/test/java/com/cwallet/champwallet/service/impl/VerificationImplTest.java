package com.cwallet.champwallet.service.impl;

import com.cwallet.champwallet.dto.account.VerificationDTO;
import com.cwallet.champwallet.exception.account.AccountAlreadyActivatedException;
import com.cwallet.champwallet.exception.account.NoSuchAccountException;
import com.cwallet.champwallet.exception.account.VerificationAlreadyUsedException;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Verification;
import com.cwallet.champwallet.repository.account.UserRepository;
import com.cwallet.champwallet.repository.account.VerificationRepository;
import com.cwallet.champwallet.service.impl.account.VerificationImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VerificationImplTest {
    @Mock
    private VerificationRepository verificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private VerificationImpl verificationService;
    @Test(expected = NoSuchAccountException.class)
    public void testValidateAccount_ShouldThrowNoSuchAccountException_WhenAccountIDDoesNotExist() throws NoSuchAccountException, VerificationAlreadyUsedException, AccountAlreadyActivatedException {
        long invalidAccountID = 1L;
        String code = "123456";
        when(userRepository.findById(invalidAccountID)).thenReturn(Optional.empty());
        verificationService.validateAccount(code, invalidAccountID);
        verify(userRepository, times(1)).findById(invalidAccountID);
    }
    @Test(expected = AccountAlreadyActivatedException.class)
    public void testValidateAccount_ShouldThrowAccountAlreadyActivatedException_WhenUserEntityIsAlreadyActivated() throws NoSuchAccountException, VerificationAlreadyUsedException, AccountAlreadyActivatedException {
        long validAccountID = 1L;
        String code = "123456";
        UserEntity user = UserEntity.builder()
                .id(validAccountID)
                .isActive(true)
                .build();
        when(userRepository.findById(validAccountID)).thenReturn(Optional.of(user));
        verificationService.validateAccount(code, validAccountID);
        verify(userRepository, times(1)).findById(validAccountID);
    }
    @Test
    public void testValidateAccount_ShouldReturnFalse_WhenNoVerificationObjectIsFound() throws NoSuchAccountException, VerificationAlreadyUsedException, AccountAlreadyActivatedException {
        long validAccountID = 1L;
        String code = "123456";
        UserEntity user = UserEntity.builder()
                .id(validAccountID)
                .isActive(false)
                .build();
        when(userRepository.findById(validAccountID)).thenReturn(Optional.of(user));
        when(verificationRepository.findLatestTimestampByAccountID(any(), any(), anyLong())).thenReturn(null);
        boolean result = verificationService.validateAccount(code, validAccountID);
        assertFalse(result);
        verify(userRepository, times(1)).findById(validAccountID);
        verify(verificationRepository, times(1)).findLatestTimestampByAccountID(any(), any(), anyLong());
    }
    @Test(expected = VerificationAlreadyUsedException.class)
    public void testValidateAccount_ShouldThrowVerificationAlreadyUsed_WhenTheVerificationCodeIsAlreadyUsed() throws NoSuchAccountException, VerificationAlreadyUsedException, AccountAlreadyActivatedException {
        long validAccountID = 1L;
        String code = "123456";
        UserEntity user = UserEntity.builder()
                .id(validAccountID)
                .isActive(false)
                .build();
        Verification verification = Verification.builder()
                .verification_code(code+"U")
                .build();
        when(userRepository.findById(validAccountID)).thenReturn(Optional.of(user));
        when(verificationRepository.findLatestTimestampByAccountID(any(), any(), anyLong())).thenReturn(verification);
        verificationService.validateAccount(code, validAccountID);
        verify(userRepository, times(1)).findById(validAccountID);
        verify(verificationRepository, times(1)).findLatestTimestampByAccountID(any(), any(), anyLong());
    }
    @Test
    public void testValidateAccount_ShouldReturnFalse_WhenTheProvidedVerificationCodeIsNotEqualToTheActualVerificationCode() throws NoSuchAccountException, VerificationAlreadyUsedException, AccountAlreadyActivatedException {
        long validAccountID = 1L;
        String incorrectCode = "123456";
        String correctCode = "654321";
        UserEntity user = UserEntity.builder()
                .id(validAccountID)
                .isActive(false)
                .build();
        Verification verification = Verification.builder()
                .verification_code(correctCode)
                .build();
        when(userRepository.findById(validAccountID)).thenReturn(Optional.of(user));
        when(verificationRepository.findLatestTimestampByAccountID(any(), any(), anyLong())).thenReturn(verification);
        boolean result = verificationService.validateAccount(incorrectCode, validAccountID);
        assertFalse(result);
        verify(userRepository, times(1)).findById(validAccountID);
        verify(verificationRepository, times(1)).findLatestTimestampByAccountID(any(), any(), anyLong());
    }
    @Test
    public void testValidateAccount_ShouldReturnTrueAndSucceedValidation() throws NoSuchAccountException, VerificationAlreadyUsedException, AccountAlreadyActivatedException {
        long validAccountID = 1L;
        String correctCode = "654321";
        UserEntity user = UserEntity.builder()
                .id(validAccountID)
                .isActive(false)
                .build();
        Verification verification = Verification.builder()
                .id(1L)
                .verification_code(correctCode)
                .user(user)
                .build();
        when(userRepository.findById(validAccountID)).thenReturn(Optional.of(user));
        when(verificationRepository.findLatestTimestampByAccountID(any(), any(), anyLong())).thenReturn(verification);
        boolean result = verificationService.validateAccount(correctCode, validAccountID);
        assertTrue(result);
        UserEntity verifiedUser = UserEntity.builder()
                .id(validAccountID)
                .isActive(true)
                .build();
        Verification usedVerification = Verification.builder()
                .id(1L)
                .verification_code(correctCode+"U")
                .user(user)
                .build();
        verify(userRepository, times(1)).findById(validAccountID);
        verify(verificationRepository, times(1)).findLatestTimestampByAccountID(any(), any(), anyLong());
        verify(userRepository, times(1)).save(verifiedUser);
        verify(verificationRepository, times(1)).save(usedVerification);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResetPassword_ShouldThrowIllegalArgumentException_WhenCodeIsEmpty() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String invalidCode = "";
        long account_id = 1L;
        String password = "password";
        verificationService.resetPassword(invalidCode, account_id, password);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResetPassword_ShouldThrowIllegalArgumentException_WhenCodeIsNull() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String invalidCode = null;
        long account_id = 1L;
        String password = "password";
        verificationService.resetPassword(invalidCode, account_id, password);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResetPassword_ShouldThrowIllegalArgumentException_WhenNewPasswordIsEmpty() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String code = "123456";
        long account_id = 1L;
        String password = "";
        verificationService.resetPassword(code, account_id, password);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testResetPassword_ShouldThrowIllegalArgumentException_WhenNewPasswordIsNull() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String code = "123456";
        long account_id = 1L;
        String password = null;
        verificationService.resetPassword(code, account_id, password);
    }
    @Test(expected = NoSuchAccountException.class)
    public void testResetPassword_ShouldThrowNoSuchAccountException_WhenAccountIDIsNotRegistered() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String code = "123456";
        long account_id = 1L;
        String password = "new_password";
        when(userRepository.findById(account_id)).thenReturn(Optional.empty());
        verificationService.resetPassword(code, account_id, password);
        verify(userRepository).findById(account_id);
    }
    @Test
    public void testResetPassword_ShouldReturnFalse_WhenThereIsNoVerificationFoundForThatUserWhereinTheCreationIsWithinFifteenMinutes() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String code = "123456";
        long account_id = 1L;
        String password = "new_password";
        when(userRepository.findById(account_id)).thenReturn(Optional.of(new UserEntity()));
        when(verificationRepository.findLatestTimestampByAccountID(any(), any(), anyLong())).thenReturn(null);
        boolean result = verificationService.resetPassword(code, account_id, password);
        assertFalse(result);
        verify(userRepository).findById(account_id);
        verify(verificationRepository).findLatestTimestampByAccountID(any(), any(), anyLong());
    }
    @Test(expected = VerificationAlreadyUsedException.class)
    public void testResetPassword_ShouldThrowVerificationAlreadyUsedException_WhenTheVerificationObjectIsAlreadyUsed() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String code = "123456";
        String usedCode = "123456U";
        long account_id = 1L;
        String password = "new_password";
        UserEntity user = UserEntity.builder()
                .id(account_id)
                .build();
        Verification verification = Verification.builder()
                .verification_code(usedCode)
                .user(user)
                .build();
        when(userRepository.findById(account_id)).thenReturn(Optional.of(user));
        when(verificationRepository.findLatestTimestampByAccountID(any(), any(), anyLong())).thenReturn(verification);
        verificationService.resetPassword(code, account_id, password);
        verify(userRepository).findById(account_id);
        verify(verificationRepository).findLatestTimestampByAccountID(any(), any(), anyLong());
    }
    @Test
    public void testResetPassword_ShouldReturnFalse_WhenTheProvidedCodeDoesntMatchWithTheVerificationCodeOnTheVerificationObject() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String providedCode = "123456";
        String actualCode = "654321";
        long account_id = 1L;
        String password = "new_password";
        UserEntity user = UserEntity.builder()
                .id(account_id)
                .build();
        Verification verification = Verification.builder()
                .verification_code(actualCode)
                .user(user)
                .build();
        when(userRepository.findById(account_id)).thenReturn(Optional.of(user));
        when(verificationRepository.findLatestTimestampByAccountID(any(), any(), anyLong())).thenReturn(verification);
        boolean result = verificationService.resetPassword(providedCode, account_id, password);
        assertFalse(result);
        verify(userRepository).findById(account_id);
        verify(verificationRepository).findLatestTimestampByAccountID(any(), any(), anyLong());
    }
    @Test
    public void testResetPassword_ShouldSucceed() throws NoSuchAccountException, VerificationAlreadyUsedException {
        String providedCode = "654321";
        String actualCode = "654321";
        String modifiedCode = actualCode + "U";
        long account_id = 1L;
        String oldPassword = "old_password";
        String newPassword = "new_password";
        String encodedPassword = "encodedPassword";
        UserEntity user = UserEntity.builder()
                .id(account_id)
                .password(oldPassword)
                .build();
        Verification verification = Verification.builder()
                .verification_code(actualCode)
                .user(user)
                .build();
        when(userRepository.findById(account_id)).thenReturn(Optional.of(user));
        when(verificationRepository.findLatestTimestampByAccountID(any(), any(), anyLong())).thenReturn(verification);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        boolean result = verificationService.resetPassword(providedCode, account_id, newPassword);
        assertTrue(result);
        Verification usedVerification = Verification.builder()
                .verification_code(modifiedCode)
                .user(user)
                .build();
        UserEntity modifiedUser = UserEntity.builder()
                .id(account_id)
                .password(encodedPassword)
                .build();
        verify(userRepository).findById(account_id);
        verify(verificationRepository).findLatestTimestampByAccountID(any(), any(), anyLong());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(modifiedUser);
        verify(verificationRepository).save(usedVerification);
    }
}
