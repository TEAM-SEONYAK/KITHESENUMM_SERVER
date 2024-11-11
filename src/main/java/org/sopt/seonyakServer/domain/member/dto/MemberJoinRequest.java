package org.sopt.seonyakServer.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MemberJoinRequest(
        @NotBlank(message = "role은 공백일 수 없습니다.")
        String role,
        @NotNull(message = "isSubscribed는 null일 수 없습니다.")
        Boolean isSubscribed,
        @NotBlank(message = "nickname은 공백일 수 없습니다.")
        String nickname,
        @NotNull(message = "image는 null일 수 없습니다.")
        String image,
        @NotBlank(message = "phoneNumber는 공백일 수 없습니다.")
        String phoneNumber,
        @NotBlank(message = "univName은 공백일 수 없습니다.")
        String univName,
        @NotBlank(message = "field는 공백일 수 없습니다.")
        String field,
        @NotEmpty(message = "departmentList는 비어 있을 수 없습니다.")
        List<@NotBlank String> departmentList,
        String businessCard,
        String company,
        String position,
        String detailPosition,
        String level
) {
}
