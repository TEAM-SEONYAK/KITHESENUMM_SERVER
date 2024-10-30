package org.sopt.seonyakServer.domain.university.service;

import lombok.RequiredArgsConstructor;
import org.sopt.seonyakServer.domain.university.model.UnivCode;
import org.sopt.seonyakServer.domain.university.repository.UnivCodeRepository;
import org.sopt.seonyakServer.global.exception.enums.ErrorType;
import org.sopt.seonyakServer.global.exception.model.CustomException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnivCodeService {
    private final UnivCodeRepository univCodeRepository;

    public void saveUnivVerificationCode(
            final String univMail,
            final String verificationCode
    ) {
        univCodeRepository.save(
                UnivCode.builder()
                        .univMail(univMail)
                        .verificationCode(verificationCode)
                        .build()
        );
    }

    public String findCodeByUnivMail(final String univMail) {
        UnivCode univCode = univCodeRepository.findByUnivMail(univMail).orElseThrow(
                () -> new CustomException(ErrorType.NO_VERIFICATION_REQUEST_HISTORY)
        );

        return univCode.getVerificationCode();
    }

    public void deleteVerificationCode(final String univMail) {
        UnivCode univCode = univCodeRepository.findByUnivMail(univMail).orElseThrow(
                () -> new CustomException(ErrorType.NO_VERIFICATION_REQUEST_HISTORY)
        );

        univCodeRepository.delete(univCode);
    }
}
