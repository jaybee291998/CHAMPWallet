package com.cwallet.champwallet.mappers.account;

import com.cwallet.champwallet.dto.account.VerificationDTO;
import com.cwallet.champwallet.models.account.Verification;

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
