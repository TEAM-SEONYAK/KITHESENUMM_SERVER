package org.sopt.seonyakServer.domain.senior.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.seonyakServer.domain.member.dto.MemberJoinRequest;
import org.sopt.seonyakServer.domain.member.model.Member;
import org.sopt.seonyakServer.domain.senior.dto.SeniorListResponse;
import org.sopt.seonyakServer.domain.senior.dto.SeniorProfileRequest;
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

    private final SeniorRepository seniorRepository;
    private final PrincipalHandler principalHandler;

    @Transactional
    public String createSenior(final MemberJoinRequest memberJoinRequest, Member member) {

        Senior senior = Senior.createSenior(
                member,
                memberJoinRequest.businessCard(),
                memberJoinRequest.detailPosition(),
                memberJoinRequest.company(),
                memberJoinRequest.position(),
                memberJoinRequest.level()
        );

        seniorRepository.save(senior);

        return memberJoinRequest.role();
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
}
