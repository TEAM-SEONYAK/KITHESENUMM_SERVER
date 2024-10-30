package org.sopt.seonyakServer.domain.university.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "UnivVerificationCode", timeToLive = 5 * 60L) // TTL 5분
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnivCode {
    @Id
    private String univMail;

    private String verificationCode;

    @Builder
    private UnivCode(
            final String univMail,
            final String verificationCode
    ) {
        this.univMail = univMail;
        this.verificationCode = verificationCode;
    }
}
