package org.sopt.seonyakServer.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberJoinResponse(
        Long seniorId,
        String role
) {
    public static MemberJoinResponse of(
            final Long seniorId,
            final String role
    ) {
        return new MemberJoinResponse(
                seniorId,
                role
        );
    }
}
