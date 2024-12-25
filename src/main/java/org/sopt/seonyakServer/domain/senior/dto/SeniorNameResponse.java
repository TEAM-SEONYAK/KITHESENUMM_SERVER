package org.sopt.seonyakServer.domain.senior.dto;

public record SeniorNameResponse(
        String name
) {

    public static SeniorNameResponse of(
            final String name
    ) {
        return new SeniorNameResponse(
                name
        );
    }
}
