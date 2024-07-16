package org.sopt.seonyakServer.domain.senior.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.seonyakServer.domain.appointment.service.AppointmentService;
import org.sopt.seonyakServer.domain.member.dto.MemberJoinRequest;
import org.sopt.seonyakServer.domain.member.model.Member;
import org.sopt.seonyakServer.domain.member.repository.MemberRepository;
import org.sopt.seonyakServer.domain.senior.dto.SeniorCardProfileResponse;
import org.sopt.seonyakServer.domain.senior.dto.SeniorListResponse;
import org.sopt.seonyakServer.domain.senior.dto.SeniorProfileRequest;
import org.sopt.seonyakServer.domain.senior.dto.SeniorProfileResponse;
import org.sopt.seonyakServer.domain.senior.model.PreferredTimeList;
import org.sopt.seonyakServer.domain.senior.model.Senior;
import org.sopt.seonyakServer.domain.senior.repository.SeniorRepository;
import org.sopt.seonyakServer.global.auth.PrincipalHandler;
import org.sopt.seonyakServer.global.exception.enums.ErrorType;
import org.sopt.seonyakServer.global.exception.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeniorService {

    private final MemberRepository memberRepository;
    private final SeniorRepository seniorRepository;
    private final AppointmentService appointmentService;
    private final PrincipalHandler principalHandler;

    @Transactional
    public Senior createSenior(final MemberJoinRequest memberJoinRequest, Member member) {

        Senior senior = Senior.create(
                member,
                memberJoinRequest.businessCard(),
                memberJoinRequest.detailPosition(),
                memberJoinRequest.company(),
                memberJoinRequest.position(),
                memberJoinRequest.level()
        );

        return seniorRepository.save(senior);
    }

    @Transactional
    public void patchSeniorProfile(SeniorProfileRequest seniorProfileRequest) {
        Senior senior = seniorRepository.findSeniorByMemberId(principalHandler.getUserIdFromPrincipal())
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_SENIOR_BY_MEMBER));

        senior.updateSenior(
                seniorProfileRequest.catchphrase(),
                seniorProfileRequest.career(),
                seniorProfileRequest.award(),
                seniorProfileRequest.story(),
                seniorProfileRequest.preferredTimeList()
        );
        seniorRepository.save(senior);
    }

    @Transactional(readOnly = true)
    public PreferredTimeList getSeniorPreferredTime(Long seniorId) {
        Senior senior = seniorRepository.findSeniorByIdOrThrow(seniorId);
        return senior.getPreferredTimeList();
    }

    @Transactional(readOnly = true)
    public List<SeniorListResponse> searchSeniorFieldPosition(List<String> field, List<String> position) {
        return seniorRepository.searchSeniorFieldPosition(field, position);
    }

    @Transactional(readOnly = true)
    public SeniorProfileResponse getSeniorProfile(final Long seniorId) {
        Member member = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal());
        Senior senior = seniorRepository.findSeniorByIdOrThrow(seniorId);

        // 해당 선배와 PENDING, SCHEDULED인 약속이 있다면 불가능하게
        boolean isAvailable = !appointmentService.isExistingAppointment(member.getId(), seniorId);

        return SeniorProfileResponse.of(
                isAvailable,
                senior.getLevel(),
                senior.getCareer(),
                senior.getAward(),
                senior.getCatchphrase(),
                senior.getStory()
        );
    }

    @Transactional(readOnly = true)
    public SeniorCardProfileResponse getSeniorCardProfile(final Long seniorId) {
        Senior senior = seniorRepository.findSeniorByIdOrThrow(seniorId);

        return SeniorCardProfileResponse.of(
                senior.getMember().getNickname(),
                senior.getCompany(),
                senior.getMember().getField(),
                senior.getPosition(),
                senior.getDetailPosition(),
                senior.getLevel()
        );
    }
}
