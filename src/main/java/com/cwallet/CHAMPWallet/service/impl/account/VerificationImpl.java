package com.cwallet.CHAMPWallet.service.impl.account;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import com.cwallet.CHAMPWallet.dto.account.VerificationDTO;
import com.cwallet.CHAMPWallet.exception.account.AccountAlreadyActivatedException;
import com.cwallet.CHAMPWallet.exception.account.NoSuchAccountException;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.account.Verification;
import com.cwallet.CHAMPWallet.repository.account.UserRepository;
import com.cwallet.CHAMPWallet.repository.account.VerificationRepository;
import com.cwallet.CHAMPWallet.service.account.VerificationService;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;

import static com.cwallet.CHAMPWallet.mappers.account.VerificationMapper.mapToVerification;
import static com.cwallet.CHAMPWallet.mappers.account.VerificationMapper.mapToVerificationDTO;

@Service
public class VerificationImpl implements VerificationService {
    private VerificationRepository verificationRepository;
    private Random rand;
    private final UserRepository userRepository;

    public VerificationImpl(VerificationRepository verificationRepository,
                            UserRepository userRepository) {
        this.verificationRepository = verificationRepository;
        rand = new Random();
        this.userRepository = userRepository;
    }
    @Override
    public String generateCode() {
        return String.valueOf(100_000 + rand.nextInt(899_999));
    }

    @Override
    public void saveVerificationCode(VerificationDTO verificationDTO) {
        Verification verification = mapToVerification(verificationDTO);
        verificationRepository.save(verification);
    }

    @Override
    public VerificationDTO requestVerification(UserEntity user) {
        return VerificationDTO.builder()
                .verification_code(generateCode())
                .user(user)
                .build();
    }

    @Override
    public boolean validateAccount(String code, long account_id) throws NoSuchAccountException, AccountAlreadyActivatedException{
        Optional<UserEntity> optionalUser = userRepository.findById(account_id);
        if(!optionalUser.isPresent()) throw new NoSuchAccountException("Account not found: " + account_id);
        UserEntity user = optionalUser.get();
        if(user.isActive()) throw new AccountAlreadyActivatedException("Account is already verified: " + account_id);
        VerificationDTO verification = getLatestValidVerificationByUser(account_id);
        if(verification == null) return false;
        if(!verification.getVerification_code().equals(code)) return false;
        user.setActive(true);
        userRepository.save(user);
        return true;
    }


    @Override
    public VerificationDTO getLatestValidVerificationByUser(long account_id) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusMinutes(15);

        Verification verification = verificationRepository.findLatestTimestampByAccountID(start, end, account_id);
        if(verification == null) return null;
        return mapToVerificationDTO(verification);
    }
}
