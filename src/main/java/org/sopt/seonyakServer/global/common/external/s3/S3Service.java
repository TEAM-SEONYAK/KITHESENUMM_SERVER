package org.sopt.seonyakServer.global.common.external.s3;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sopt.seonyakServer.domain.member.model.Member;
import org.sopt.seonyakServer.domain.member.repository.MemberRepository;
import org.sopt.seonyakServer.domain.senior.repository.SeniorRepository;
import org.sopt.seonyakServer.global.auth.PrincipalHandler;
import org.sopt.seonyakServer.global.common.external.s3.dto.PreSignedUrlResponse;
import org.sopt.seonyakServer.global.exception.enums.ErrorType;
import org.sopt.seonyakServer.global.exception.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3Service {

    @Value("${aws-property.s3-bucket-name}")
    private String bucketName;

    @Value("${aws-property.s3-substring}")
    private String s3Substring;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final PrincipalHandler principalHandler;
    private final MemberRepository memberRepository;
    private final SeniorRepository seniorRepository;

    // PreSigned URL 만료시간 60분
    private static final Long PRE_SIGNED_URL_EXPIRE_MINUTE = 60L;
    // 파일 확장자 제한 jpeg, png, jpg, webp
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("image/jpeg", "image/png", "image/jpg",
            "image/webp");
    // 단일 PUT 요청 파일 크기 제한 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024L * 1024L;
    private final static String profilePath = "profiles/";
    private final static String businessPath = "businessCard/";

    public PreSignedUrlResponse getImageUploadPreSignedUrl() {
        try {
            // UUID 파일명 생성
            String uuidFileName = UUID.randomUUID().toString() + ".jpg";
            // 경로 + 파일 이름
            String key = profilePath + uuidFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // S3에서 업로드는 PUT 요청
            PutObjectPresignRequest preSignedUrlRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(PRE_SIGNED_URL_EXPIRE_MINUTE))
                    .putObjectRequest(putObjectRequest)
                    .build();

            // Persigned URL 생성
            URL url = s3Presigner.presignPutObject(preSignedUrlRequest).url();

            return PreSignedUrlResponse.of(uuidFileName, url.toString());

        } catch (RuntimeException e) {
            throw new CustomException(ErrorType.GET_UPLOAD_PRESIGNED_URL_ERROR);
        }
    }

    public PreSignedUrlResponse getUploadBusinesscardPreSignedUrl() {
        try {
            // UUID 파일명 생성
            String uuidFileName = UUID.randomUUID().toString() + ".jpg";
            // 경로 + 파일 이름
            String key = businessPath + uuidFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // S3에서 업로드는 PUT 요청
            PutObjectPresignRequest preSignedUrlRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(PRE_SIGNED_URL_EXPIRE_MINUTE))
                    .putObjectRequest(putObjectRequest)
                    .build();

            // Persigned URL 생성
            URL url = s3Presigner.presignPutObject(preSignedUrlRequest).url();

            return PreSignedUrlResponse.of(uuidFileName, url.toString());

        } catch (RuntimeException e) {
            throw new CustomException(ErrorType.GET_UPLOAD_PRESIGNED_URL_ERROR);
        }
    }

    @Transactional
    public void uploadProfile(MultipartFile profileImage) {
        validateExtension(profileImage);
        validateFileSize(profileImage);

        // UUID 파일명 생성
        String uuidFileName = UUID.randomUUID().toString() + ".jpg";
        // 경로 + 파일 이름
        String key = profilePath + uuidFileName;
        Member member = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal());
        member.addProfile("https://" + bucketName + s3Substring + key);
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(profileImage.getContentType())
                    .contentDisposition("inline")
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(profileImage.getBytes());
            s3Client.putObject(request, requestBody);
        } catch (Exception e) {
            throw new CustomException(ErrorType.S3_UPLOAD_ERROR);
        }
    }

    @Transactional
    public void uploadBusinessCard(MultipartFile businessCardImage) {
        validateExtension(businessCardImage);
        validateFileSize(businessCardImage);

        // UUID 파일명 생성
        String uuidFileName = UUID.randomUUID().toString() + ".jpg";
        // 경로 + 파일 이름
        String key = businessPath + uuidFileName;

        /**
         * 회원가입 도중이라 Senior 엔티티가 아직 DB에 추가되지 않았음에도 해당 엔티티의 id를 참조하므로 NullPointerException 발생,
         * Presigned URL 구현 이전 임시 용도로 구성된 API로 구현 완료 시 사용하지 않게 될 API이므로 수정하기보단 주석 처리를 해놓겠습니다
         */
//        Member member = memberRepository.findMemberByIdOrThrow(principalHandler.getUserIdFromPrincipal());
//        Senior senior = seniorRepository.findSeniorByIdOrThrow(member.getSenior().getId());
//        senior.addBusinessCard("https://" + bucketName + s3Substring + key);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(businessCardImage.getContentType())
                    .contentDisposition("inline")
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(businessCardImage.getBytes());
            s3Client.putObject(request, requestBody);
        } catch (Exception e) {
            throw new CustomException(ErrorType.S3_UPLOAD_ERROR);
        }
    }

    // 파일 확장자 검증 (서버에 직접 이미지를 전송하는 경우)
    private void validateExtension(MultipartFile image) {
        String contentType = image.getContentType();
        if (!IMAGE_EXTENSIONS.contains(contentType)) {
            throw new CustomException(ErrorType.IMAGE_EXTENSION_ERROR);
        }
    }

    // 파일 최대 크기 검증 (서버에 직접 이미지를 전송하는 경우)
    private void validateFileSize(MultipartFile image) {
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ErrorType.IMAGE_SIZE_ERROR);
        }
    }
}
