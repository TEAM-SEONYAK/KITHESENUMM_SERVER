package org.sopt.seonyakServer.domain.appointment.service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.sopt.seonyakServer.domain.appointment.dto.AppointmentAcceptRequest;
import org.sopt.seonyakServer.domain.appointment.dto.AppointmentDetailResponse;
import org.sopt.seonyakServer.domain.appointment.dto.AppointmentRejectRequest;
import org.sopt.seonyakServer.domain.appointment.dto.AppointmentRequest;
import org.sopt.seonyakServer.domain.appointment.dto.AppointmentResponse;
import org.sopt.seonyakServer.domain.appointment.dto.GoogleMeetLinkResponse;
import org.sopt.seonyakServer.domain.appointment.model.Appointment;
import org.sopt.seonyakServer.domain.appointment.model.AppointmentCard;
import org.sopt.seonyakServer.domain.appointment.model.AppointmentCardList;
import org.sopt.seonyakServer.domain.appointment.model.AppointmentStatus;
import org.sopt.seonyakServer.domain.appointment.model.DateTimeRange;
import org.sopt.seonyakServer.domain.appointment.model.JuniorInfo;
import org.sopt.seonyakServer.domain.appointment.model.SeniorInfo;
import org.sopt.seonyakServer.domain.appointment.repository.AppointmentRepository;
import org.sopt.seonyakServer.domain.member.model.Member;
import org.sopt.seonyakServer.domain.member.repository.MemberRepository;
import org.sopt.seonyakServer.domain.senior.model.Senior;
import org.sopt.seonyakServer.domain.senior.repository.SeniorRepository;
import org.sopt.seonyakServer.global.auth.PrincipalHandler;
import org.sopt.seonyakServer.global.exception.enums.ErrorType;
import org.sopt.seonyakServer.global.exception.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SeniorRepository seniorRepository;
    private final MemberRepository memberRepository;
    private final PrincipalHandler principalHandler;

    private DefaultMessageService defaultMessageService;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        this.defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    @Transactional
    public void postAppointment(AppointmentRequest appointmentRequest) {
        Member member = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal());
        Senior senior = seniorRepository.findSeniorByIdOrThrow(appointmentRequest.seniorId());

        // 자기 자신에게 약속을 신청하는 경우
        if (member.getId().equals(senior.getMember().getId())) {
            throw new CustomException(ErrorType.SAME_MEMBER_APPOINTMENT_ERROR);
        }

        // 이미 약속을 신청한 선배일 경우
        if (isExistingAppointment(member.getId(), senior.getId())) {
            throw new CustomException(ErrorType.INVALID_SAME_SENIOR);
        }

        // 두 고민이 전부 넘어온 경우
        if ((appointmentRequest.topic() != null && !appointmentRequest.topic().isEmpty()) && (
                appointmentRequest.personalTopic() != null && !appointmentRequest.personalTopic().isBlank())) {
            throw new CustomException(ErrorType.INVALID_BOTH_TOPICS_PROVIDED);
        }

        // 두 고민이 전부 빈 값인 경우
        if ((appointmentRequest.topic() == null || appointmentRequest.topic().isEmpty()) && (
                appointmentRequest.personalTopic() == null || appointmentRequest.personalTopic().isBlank())) {
            throw new CustomException(ErrorType.INVALID_NO_TOPIC_PROVIDED);
        }

        Appointment appointment = Appointment.builder()
                .member(member)
                .senior(senior)
                .appointmentStatus(AppointmentStatus.PENDING)
                .timeList(appointmentRequest.timeList())
                .topic(appointmentRequest.topic())
                .personalTopic(appointmentRequest.personalTopic())
                .build();

        appointmentRepository.save(appointment);

        sendNoticeMessage(
                appointment.getSenior().getMember(),
                "'" + appointment.getMember().getNickname() + "' 후배님이 약속을 신청하셨습니다."
        );
    }

    @Transactional
    public void acceptAppointment(AppointmentAcceptRequest appointmentAcceptRequest) {
        Appointment appointment = appointmentRepository.findAppointmentByIdOrThrow(
                appointmentAcceptRequest.appointmentId()
        );
        Member member = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal());

        // 확정 대기 상태의 약속이 아닌 경우
        if (appointment.getAppointmentStatus() != AppointmentStatus.PENDING) {
            throw new CustomException(ErrorType.NOT_PENDING_APPOINTMENT_ERROR);
        }

        // 약속의 선배 Id와 토큰 Id가 일치하지 않는 경우
        if (!Objects.equals(member.getId(), appointment.getSenior().getMember().getId())) {
            throw new CustomException(ErrorType.NOT_AUTHORIZATION_ACCEPT);
        }

        appointment.acceptAppointment(
                appointmentAcceptRequest.timeList(),
                appointmentAcceptRequest.googleMeetLink(),
                AppointmentStatus.SCHEDULED
        );

        sendNoticeMessage(
                appointment.getMember(),
                "'" + appointment.getSenior().getMember().getNickname() + "' 선배님이 약속을 수락하셨습니다."
        );
    }

    @Transactional
    public void rejectAppointment(AppointmentRejectRequest appointmentRejectRequest) {
        Appointment appointment = appointmentRepository.findAppointmentByIdOrThrow(
                appointmentRejectRequest.appointmentId()
        );
        Member member = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal());

        // 확정 대기 상태의 약속이 아닌 경우
        if (appointment.getAppointmentStatus() != AppointmentStatus.PENDING) {
            throw new CustomException(ErrorType.NOT_PENDING_APPOINTMENT_ERROR);
        }

        // 약속의 선배 Id와 토큰 Id가 일치하지 않는 경우
        if (!Objects.equals(member.getId(), appointment.getSenior().getMember().getId())) {
            throw new CustomException(ErrorType.NOT_AUTHORIZATION_REJECT);
        }

        appointment.rejectAppointment(
                appointmentRejectRequest.rejectReason(),
                appointmentRejectRequest.rejectDetail(),
                AppointmentStatus.REJECTED
        );

        sendNoticeMessage(
                appointment.getMember(),
                "'" + appointment.getSenior().getMember().getNickname() + "' 선배님이 약속을 거절하셨습니다."
        );
    }

    public void sendNoticeMessage(Member member, String messageDetail) {
        Message message = new Message();

        message.setFrom(fromNumber);
        message.setTo(member.getPhoneNumber());
        message.setText("[선약] " + messageDetail);

        this.defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
    }

    @Transactional
    public GoogleMeetLinkResponse getGoogleMeetLink(Long appointmentId) {
        Long userId = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal()).getId();

        Appointment appointment = appointmentRepository.findAppointmentByIdOrThrow(appointmentId);
        Long memberId = appointment.getMember().getId();
        Long seniorMemberId = appointment.getSenior().getMember().getId();

        if (!userId.equals(memberId) && !userId.equals(seniorMemberId)) {
            throw new CustomException(ErrorType.NOT_MEMBERS_APPOINTMENT_ERROR);
        }

        String googleMeetLink = appointment.getGoogleMeetLink();

        if (googleMeetLink == null || googleMeetLink.isEmpty()) {
            throw new CustomException(ErrorType.NOT_FOUND_GOOGLE_MEET_LINK_ERROR);
        }

        appointment.setAppointmentPast();
        return GoogleMeetLinkResponse.of(googleMeetLink);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment() {

        Member user = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal());
        AppointmentCardList appointmentCardList = new AppointmentCardList();
        List<Appointment> appointmentList;

        // User의 약속 리스트를 가져옴
        if (user.getSenior() == null) {
            appointmentList = appointmentRepository.findAllAppointmentByMember(user);
        } else {
            appointmentList = appointmentRepository.findAllAppointmentBySenior(user.getSenior());
        }
        for (Appointment appointment : appointmentList) {
            appointmentCardList.putAppointmentCardList(
                    appointment.getAppointmentStatus(),
                    createAppointmentCard(user, appointment)
            );
        }

        return AppointmentResponse.of(user.getNickname(), appointmentCardList);
    }

    private AppointmentCard createAppointmentCard(Member user, Appointment appointment) {
        // init
        Long seniorId = null;
        String nickname, image, field, department = null;
        List<String> topic = null;
        String personalTopic = null;
        String company = null, position = null, detailPosition = null, level = null;
        String date = null, startTime = null, endTime = null;

        DateTimeRange dateTimeRange = appointment.getTimeList().get(0);

        // 선배/후배에 따른 분기 처리
        if (user.getSenior() == null) {
            Senior senior = appointment.getSenior();
            Member seniorMember = senior.getMember();
            seniorId = senior.getId();
            nickname = seniorMember.getNickname();
            image = seniorMember.getImage();
            field = seniorMember.getField();
            company = senior.getCompany();
            position = senior.getPosition();
            detailPosition = senior.getDetailPosition();
            level = senior.getLevel();
        } else {
            Member member = appointment.getMember();
            nickname = member.getNickname();
            image = member.getImage();
            field = member.getField();
            department = member.getDepartmentList().get(0);
            topic = appointment.getTopic();
            personalTopic = appointment.getPersonalTopic();
        }

        // 약속 상태에 따른 분기 처리
        if (appointment.getAppointmentStatus().isScheduledOrPast()) {
            date = dateTimeRange.getDate();
            startTime = dateTimeRange.getStartTime();
            endTime = dateTimeRange.getEndTime();
        }

        if (appointment.getAppointmentStatus().isPastOrRejected()) {
            topic = null;
            personalTopic = null;
        }

        // Appointment에서 필요한 필드들을 매핑
        return AppointmentCard.builder()
                .appointmentId(appointment.getId())
                .appointmentStatus(appointment.getAppointmentStatus())
                .seniorId(seniorId)
                .nickname(nickname)
                .image(image)
                .field(field)
                .department(department)
                .topic(topic)
                .personalTopic(personalTopic)
                .company(company)
                .position(position)
                .detailPosition(detailPosition)
                .level(level)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public AppointmentDetailResponse getAppointmentDetail(
            final Long appointmentId
    ) {
        Long userId = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal()).getId();

        Appointment appointment = appointmentRepository.findAppointmentByIdOrThrow(appointmentId);

        Member member = appointment.getMember();
        Senior senior = appointment.getSenior();
        Member seniorMember = senior.getMember();

        if (!userId.equals(member.getId()) && !userId.equals(senior.getMember().getId())) {
            throw new CustomException(ErrorType.NOT_MEMBERS_APPOINTMENT_ERROR);
        }

        JuniorInfo juniorInfo = JuniorInfo.builder()
                .nickname(member.getNickname())
                .univName(member.getUnivName())
                .field(member.getField())
                .department(member.getDepartmentList().get(0))
                .build();

        SeniorInfo seniorInfo = SeniorInfo.builder()
                .nickname(seniorMember.getNickname())
                .image(seniorMember.getImage())
                .company(senior.getCompany())
                .field(seniorMember.getField())
                .position(senior.getPosition())
                .detailPosition(senior.getDetailPosition())
                .level(senior.getLevel())
                .build();

        return new AppointmentDetailResponse(
                appointment.getAppointmentStatus(),
                juniorInfo,
                seniorInfo,
                appointment.getTopic(),
                appointment.getPersonalTopic(),
                appointment.getTimeList()
        );
    }

    // 멤버와 선배 ID로 PENDING, SCHEDULED 인 약속이 이미 존재하는지 확인
    @Transactional(readOnly = true)
    public boolean isExistingAppointment(
            final Long memberId,
            final Long seniorId
    ) {
        return appointmentRepository.findAppointmentByMemberIdAndSeniorIdAndAppointmentStatusIn(memberId,
                seniorId, Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.SCHEDULED)).isPresent();
    }
}
