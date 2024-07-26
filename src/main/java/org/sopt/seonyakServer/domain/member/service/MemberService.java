package org.sopt.seonyakServer.domain.member.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.sopt.seonyakServer.domain.member.dto.LoginSuccessResponse;
import org.sopt.seonyakServer.domain.member.dto.MemberJoinRequest;
import org.sopt.seonyakServer.domain.member.dto.MemberJoinResponse;
import org.sopt.seonyakServer.domain.member.dto.NicknameRequest;
import org.sopt.seonyakServer.domain.member.dto.SendCodeRequest;
import org.sopt.seonyakServer.domain.member.dto.VerifyCodeRequest;
import org.sopt.seonyakServer.domain.member.model.Member;
import org.sopt.seonyakServer.domain.member.model.SocialType;
import org.sopt.seonyakServer.domain.member.repository.MemberRepository;
import org.sopt.seonyakServer.domain.senior.service.SeniorService;
import org.sopt.seonyakServer.global.auth.MemberAuthentication;
import org.sopt.seonyakServer.global.auth.PrincipalHandler;
import org.sopt.seonyakServer.global.auth.jwt.JwtTokenProvider;
import org.sopt.seonyakServer.global.auth.redis.service.CodeService;
import org.sopt.seonyakServer.global.common.external.client.dto.MemberInfoResponse;
import org.sopt.seonyakServer.global.common.external.client.dto.MemberLoginRequest;
import org.sopt.seonyakServer.global.common.external.client.service.GoogleSocialService;
import org.sopt.seonyakServer.global.exception.enums.ErrorType;
import org.sopt.seonyakServer.global.exception.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalHandler principalHandler;
    private final GoogleSocialService googleSocialService;
    private final SeniorService seniorService;
    private DefaultMessageService defaultMessageService;
    private final CodeService codeService;

    private final EntityManager entityManager;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    private static final String NICKNAME_PATTERN = "^[a-zA-Z0-9가-힣]{2,8}$";
    private static final String PHONE_NUMBER_PATTERN = "^010\\d{8}$";

    @PostConstruct
    public void init() {
        this.defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    // JWT Access Token 생성
    @Transactional
    public LoginSuccessResponse create(
            final String authorizationCode,
            final MemberLoginRequest loginRequest
    ) {
        return getTokenDto(
                getMemberInfoResponse(authorizationCode, loginRequest)
        );
    }

    // 소셜 플랫폼으로부터 해당 유저 정보를 받아옴
    private MemberInfoResponse getMemberInfoResponse(
            final String authorizationCode,
            final MemberLoginRequest loginRequest
    ) {
        if (loginRequest.socialType() == SocialType.GOOGLE) {
            return googleSocialService.login(authorizationCode, loginRequest);
        }
        throw new CustomException(ErrorType.INVALID_SOCIAL_TYPE_ERROR);
    }

    // Access Token을 생성할 때, 해당 유저의 회원가입 여부를 판단
    private LoginSuccessResponse getTokenDto(final MemberInfoResponse memberInfoResponse) {
        try {
            if (isExistingMember(memberInfoResponse.socialType(), memberInfoResponse.socialId())) {
                Member member = memberRepository.findBySocialTypeAndSocialIdOrThrow(
                        memberInfoResponse.socialType(),
                        memberInfoResponse.socialId()
                );

                String role = null;

                if (member.getSenior() == null) {
                    if (member.getPhoneNumber() != null) { // 멤버 엔티티에 전화번호가 없으면 온보딩을 완료하지 못한 것으로 간주
                        role = "JUNIOR";
                    }
                } else {
                    role = "SENIOR";
                }

                return getTokenByMemberId(
                        role,
                        member.getId()
                );
            } else {
                Member member = Member.builder()
                        .socialType(memberInfoResponse.socialType())
                        .socialId(memberInfoResponse.socialId())
                        .email(memberInfoResponse.email())
                        .build();

                return getTokenByMemberId(null, memberRepository.save(member).getId());
            }
        } catch (DataIntegrityViolationException e) { // DB 무결성 제약 조건 위반 예외
            Member member = memberRepository.findBySocialTypeAndSocialIdOrThrow(
                    memberInfoResponse.socialType(),
                    memberInfoResponse.socialId()
            );

            String role = null;

            if (member.getSenior() == null) {
                if (member.getPhoneNumber() != null) { // 멤버 엔티티에 전화번호가 없으면 온보딩을 완료하지 못한 것으로 간주
                    role = "JUNIOR";
                }
            } else {
                role = "SENIOR";
            }

            return getTokenByMemberId(
                    role,
                    member.getId()
            );
        }
    }

    private boolean isExistingMember(
            final SocialType socialType,
            final String socialId
    ) {
        return memberRepository.findBySocialTypeAndSocialId(socialType, socialId).isPresent();
    }

    public LoginSuccessResponse getTokenByMemberId(
            final String role,
            final Long id
    ) {
        MemberAuthentication memberAuthentication = new MemberAuthentication(id, null, null);

        return LoginSuccessResponse.of(role, jwtTokenProvider.issueAccessToken(memberAuthentication));
    }

    // 닉네임 유효성 검증
    @Transactional(readOnly = true)
    public void validNickname(final NicknameRequest nicknameRequest) {
        if (!nicknameRequest.nickname().matches(NICKNAME_PATTERN)) { // 형식 체크
            throw new CustomException(ErrorType.INVALID_NICKNAME_ERROR);
        }

        if (memberRepository.existsByNickname(nicknameRequest.nickname())) { // 중복 체크
            throw new CustomException(ErrorType.NICKNAME_DUP_ERROR);
        }
    }

    @Transactional
    public MemberJoinResponse patchMemberJoin(MemberJoinRequest memberJoinRequest) {
        Member member = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal());

        log.info("영속성 컨텍스트에 포함되어 있나요(업데이트 이전)? " + entityManager.contains(member));

        member.updateMember(
                memberJoinRequest.isSubscribed(),
                memberJoinRequest.nickname(),
                memberJoinRequest.image(),
                memberJoinRequest.phoneNumber(),
                memberJoinRequest.univName(),
                memberJoinRequest.field(),
                memberJoinRequest.departmentList()
        );
        memberRepository.save(member);

        log.info("영속성 컨텍스트에 포함되어 있나요(업데이트 이후)? " + entityManager.contains(member));
        log.info("role: " + memberJoinRequest.role());

        Long seniorId = null;

        if ("SENIOR".equals(memberJoinRequest.role())) {
            member.addSenior(seniorService.createSenior(memberJoinRequest, member));
            seniorId = member.getSenior().getId();
        } else if (!"JUNIOR".equals(memberJoinRequest.role())) {
            throw new CustomException(ErrorType.INVALID_USER_TYPE_ERROR);
        }

        log.info("제대로 저장됐나요? 닉네임 필드를 통해 확인: "
                + memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal()).getNickname());

        return MemberJoinResponse.of(
                seniorId,
                memberJoinRequest.role()
        );
    }

    @Transactional
    public void sendMessage(SendCodeRequest sendCodeRequest) {
        Message message = new Message();

        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 함.
        String toNumber = sendCodeRequest.phoneNumber().replaceAll("-", "");

        if (!toNumber.matches(PHONE_NUMBER_PATTERN)) {
            throw new CustomException(ErrorType.INVALID_PHONE_NUMBER_ERROR);
        }

        message.setFrom(fromNumber);
        message.setTo(toNumber);

        String verificationCode = generateRandomNumber(4);
        message.setText("[선약] 인증번호는 [" + verificationCode + "] 입니다.");

        this.defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
        codeService.saveVerificationCode(toNumber, verificationCode);
    }

    // 인증번호를 위한 랜덤 숫자 생성
    private String generateRandomNumber(int digitCount) {
        Random random = new Random();
        int min = (int) Math.pow(10, digitCount - 1);
        int max = (int) Math.pow(10, digitCount) - 1;

        return String.valueOf(random.nextInt((max - min) + 1) + min);
    }

    // 인증번호 일치 여부 확인
    @Transactional
    public void verifyCode(VerifyCodeRequest verifyCodeRequest) {
        String number = verifyCodeRequest.phoneNumber().replaceAll("-", "");

        if (verifyCodeRequest.verificationCode().equals(codeService.findCodeByPhoneNumber(number))) {
            codeService.deleteVerificationCode(number);

            // 휴대전화 중복 체크
            validPhoneNumberDuplication(number);
        } else {
            throw new CustomException(ErrorType.INVALID_VERIFICATION_CODE_ERROR);
        }
    }

    private void validPhoneNumberDuplication(String phoneNumber) {
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomException(ErrorType.PHONE_NUMBER_DUP_ERROR);
        }
    }

    @Scheduled(fixedRate = 43200000) // 12시간마다 실행 (43200000 밀리초)
    @Transactional
    public void deleteMembersWithNullPhoneNumber() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusMinutes(60);
        memberRepository.deleteByPhoneNumberIsNullAndUpdatedAtBefore(oneHourAgo);
    }
}
