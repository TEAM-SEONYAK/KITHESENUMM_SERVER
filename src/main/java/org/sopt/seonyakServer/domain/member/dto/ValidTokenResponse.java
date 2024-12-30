package org.sopt.seonyakServer.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidTokenResponse(
        String nickname,
        Boolean isPending
) {

    public static ValidTokenResponse of(
            final String nickname,
            final Boolean isPending
    ) {
        return new ValidTokenResponse(
                nickname,
                isPending
        );
    }
}
