package org.sopt.seonyakServer.domain.appointment.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AppointmentStatus {

    PENDING("PENDING"),
    SCHEDULED("SCHEDULED"),
    PAST("PAST"),
    REJECTED("REJECTED");

    private final String appointmentStatus;

    public boolean isScheduledOrPast() {
        return this == SCHEDULED || this == PAST;
    }

    public boolean isPastOrRejected() {
        return this == PAST || this == REJECTED;
    }
}
