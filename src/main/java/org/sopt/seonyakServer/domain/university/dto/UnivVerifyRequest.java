package org.sopt.seonyakServer.domain.university.dto;

public record UnivVerifyRequest(
        String univName,
        String univMail
) {
}
