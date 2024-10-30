package org.sopt.seonyakServer.domain.university.dto;

public record UnivVerifyCodeRequest(
        String univEmail,
        String verificationCode
) {
}
