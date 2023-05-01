package com.cwallet.CHAMPWallet.service.impl;
import java.util.Random;
import com.cwallet.CHAMPWallet.dto.UserEntityDTO;
import com.cwallet.CHAMPWallet.dto.VerificationDTO;
import com.cwallet.CHAMPWallet.models.UserEntity;
import com.cwallet.CHAMPWallet.models.Verification;
import com.cwallet.CHAMPWallet.repository.VerificationRepository;
import com.cwallet.CHAMPWallet.service.VerificationService;
import org.springframework.stereotype.Service;

import static com.cwallet.CHAMPWallet.mappers.VerificationMapper.mapToVerification;
@Service
public class VerificationImpl implements VerificationService {
    private VerificationRepository verificationRepository;
    private Random rand;
    public VerificationImpl(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
        rand = new Random();
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
}
