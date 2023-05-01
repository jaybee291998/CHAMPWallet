package com.cwallet.CHAMPWallet.mappers;

import com.cwallet.CHAMPWallet.dto.VerificationDTO;
import com.cwallet.CHAMPWallet.models.Verification;

public class VerificationMapper {
    public static Verification mapToVerification(VerificationDTO verificationDTO) {
        return Verification.builder()
                .id(verificationDTO.getId())
                .user(verificationDTO.getUser())
                .verification_code(verificationDTO.getVerification_code())
                .timestamp(verificationDTO.getTimestamp())
                .build();
    }

    public static VerificationDTO mapToVerificationDTO(Verification verification) {
        return VerificationDTO.builder()
                .id(verification.getId())
                .user(verification.getUser())
                .verification_code(verification.getVerification_code())
                .timestamp(verification.getTimestamp())
                .build();
    }
}
