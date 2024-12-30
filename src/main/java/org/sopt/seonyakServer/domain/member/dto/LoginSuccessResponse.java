package org.sopt.seonyakServer.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginSuccessResponse(
        String role,
        String accessToken
) {

    public static LoginSuccessResponse of(
            final String role,
            final String accessToken
    ) {
        return new LoginSuccessResponse(
                role,
                accessToken
        );
    }
}
