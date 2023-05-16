package com.cwallet.champwallet.service.impl.account;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import com.cwallet.champwallet.dto.account.VerificationDTO;
import com.cwallet.champwallet.exception.account.AccountAlreadyActivatedException;
import com.cwallet.champwallet.exception.account.NoSuchAccountException;
import com.cwallet.champwallet.exception.account.VerificationAlreadyUsedException;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Verification;
import com.cwallet.champwallet.repository.account.UserRepository;
import com.cwallet.champwallet.repository.account.VerificationRepository;
import com.cwallet.champwallet.service.account.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.cwallet.champwallet.mappers.account.VerificationMapper.mapToVerification;
import static com.cwallet.champwallet.mappers.account.VerificationMapper.mapToVerificationDTO;

@Service
public class VerificationImpl implements VerificationService {
    private VerificationRepository verificationRepository;
    private Random rand;
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public VerificationImpl(VerificationRepository verificationRepository,
                            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.verificationRepository = verificationRepository;
        rand = new Random();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    public boolean validateAccount(String code, long account_id) throws NoSuchAccountException, AccountAlreadyActivatedException, VerificationAlreadyUsedException {
        Optional<UserEntity> optionalUser = userRepository.findById(account_id);
        if(!optionalUser.isPresent()) throw new NoSuchAccountException("Account not found: " + account_id);
        UserEntity user = optionalUser.get();
        if(user.isActive()) throw new AccountAlreadyActivatedException("Account is already verified: " + account_id);
        VerificationDTO verification = getLatestValidVerificationByUser(account_id);
        if(verification == null) return false;
        if(verification.getVerification_code().length() > 6) throw new VerificationAlreadyUsedException("already used");
        if(!verification.getVerification_code().equals(code)) return false;
        user.setActive(true);
        verification.setVerification_code(verification.getVerification_code()+"U");
        userRepository.save(user);
        verificationRepository.save(mapToVerification(verification));
        return true;
    }

    @Override
    public boolean resetPassword(String code, long account_id, String newPassword) throws NoSuchAccountException, VerificationAlreadyUsedException {
        if(code == null || code.equals("")) throw new IllegalArgumentException("Code must not be null or empty");
        if(newPassword == null || newPassword.equals("")) throw new IllegalArgumentException("Password must not be null or empty");
        Optional<UserEntity> optionalUser = userRepository.findById(account_id);
        if(!optionalUser.isPresent()) throw new NoSuchAccountException("Account not found: " + account_id);
        UserEntity user = optionalUser.get();
        VerificationDTO verification = getLatestValidVerificationByUser(account_id);
        if(verification == null) return false;
        if(verification.getVerification_code().length() > 6) throw new VerificationAlreadyUsedException("already used");
        if(!verification.getVerification_code().equals(code)) return false;
        user.setPassword(passwordEncoder.encode(newPassword));
        verification.setVerification_code(verification.getVerification_code()+"U");
        userRepository.save(user);
        verificationRepository.save(mapToVerification(verification));
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
