package org.sopt.seonyakServer.domain.university.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.seonyakServer.domain.university.dto.SearchDeptResponse;
import org.sopt.seonyakServer.domain.university.dto.SearchUnivResponse;
import org.sopt.seonyakServer.domain.university.dto.UnivVerifyCodeRequest;
import org.sopt.seonyakServer.domain.university.dto.UnivVerifyRequest;
import org.sopt.seonyakServer.domain.university.model.Department;
import org.sopt.seonyakServer.domain.university.model.UniversityEmail;
import org.sopt.seonyakServer.domain.university.repository.DeptRepository;
import org.sopt.seonyakServer.domain.university.repository.UnivRepository;
import org.sopt.seonyakServer.domain.university.repository.UniversityEmailRepository;
import org.sopt.seonyakServer.global.exception.enums.ErrorType;
import org.sopt.seonyakServer.global.exception.model.CustomException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnivService {

    private final UnivRepository univRepository;
    private final DeptRepository deptRepository;
    private final JavaMailSender javaMailSender;
    private final UniversityEmailRepository universityEmailRepository;
    private final UnivCodeService univCodeService;

    public SearchUnivResponse searchUniv(final String univNamePart) {
        if (univNamePart == null || univNamePart.trim().isEmpty()) {
            return SearchUnivResponse.of(List.of());
        }

        return SearchUnivResponse.of(univRepository.findByUnivNameContaining(univNamePart));
    }

    public List<SearchDeptResponse> searchDept(
            final String univName,
            final String deptName
    ) {
        if (!univRepository.existsByUnivName(univName)) {
            throw new CustomException(ErrorType.INVALID_UNIV_NAME_ERROR);
        }

        List<Department> departments = deptRepository.findByUnivIdAndDeptNameContaining(univName, deptName);
        Set<String> uniqueDeptNames = new HashSet<>();

        return departments.stream()
                .filter(department -> uniqueDeptNames.add(department.getDeptName()))
                .map(department -> SearchDeptResponse.of(
                        department.getDeptName(),
                        department.isClosed())
                )
                .collect(Collectors.toList());
    }

    public void verifyEmail(UnivVerifyRequest univVerifyRequest) {
        // 학교 이름이 테이블에 있는지
        if (!universityEmailRepository.existsByUnivName(univVerifyRequest.univName())) {
            throw new CustomException(ErrorType.NOT_FOUND_UNIV_NAME_ERROR);
        }

        UniversityEmail universityEmail = universityEmailRepository.findUniversityEmailByUnivNameOrThrow(
                univVerifyRequest.univName());

        // 사용자가 제공한 이메일 주소의 도메인 추출
        String userDomain = univVerifyRequest.univMail().split("@")[1];

        // 학교 이메일 도메인과 비교
        if (!userDomain.equals(universityEmail.getEmailDomain())) {
            throw new CustomException(ErrorType.INVALID_EMAIL_DOMAIN_ERROR);
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(univVerifyRequest.univMail());
            mimeMessageHelper.setSubject("선약 인증번호");
            String verificationCode = generateRandomNumber(4);
            mimeMessageHelper.setText("[선약] 인증번호는 [" + verificationCode + "] 입니다.", false);
            javaMailSender.send(mimeMessage);
            univCodeService.saveUnivVerificationCode(univVerifyRequest.univMail(), verificationCode);
        } catch (MessagingException e) {
            throw new CustomException(ErrorType.SMTP_ERROR);
        }
    }

    // 인증번호 일치 여부 확인
    @Transactional
    public void verifyCode(UnivVerifyCodeRequest univVerifyCodeRequest) {

        if (univVerifyCodeRequest.verificationCode()
                .equals(univCodeService.findCodeByUnivMail(univVerifyCodeRequest.univEmail()))) {
            univCodeService.deleteVerificationCode(univVerifyCodeRequest.univEmail());

        } else {
            throw new CustomException(ErrorType.INVALID_VERIFICATION_CODE_ERROR);
        }
    }

    private String generateRandomNumber(int digitCount) {
        Random random = new Random();
        int min = (int) Math.pow(10, digitCount - 1);
        int max = (int) Math.pow(10, digitCount) - 1;

        return String.valueOf(random.nextInt((max - min) + 1) + min);
    }
}
