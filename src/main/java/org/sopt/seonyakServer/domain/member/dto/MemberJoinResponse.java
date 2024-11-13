package org.sopt.seonyakServer.domain.member.dto;

public record MemberJoinResponse(
        String role
) {
    public static MemberJoinResponse of(final String role) {
        return new MemberJoinResponse(role);
    }
}
