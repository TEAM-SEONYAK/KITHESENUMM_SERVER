package org.sopt.seonyakServer.global.exception.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorType {

    /**
     * 400 BAD REQUEST
     */
    // 표준 오류
    REQUEST_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "40001", "잘못된 요청입니다."),
    INVALID_TYPE_ERROR(HttpStatus.BAD_REQUEST, "40002", "잘못된 타입이 입력되었습니다."),
    INVALID_MISSING_HEADER_ERROR(HttpStatus.BAD_REQUEST, "40003", "요청에 필요한 헤더값이 존재하지 않습니다."),
    INVALID_HTTP_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "40004", "요청 형식이 허용된 형식과 다릅니다."),
    INVALID_HTTP_METHOD_ERROR(HttpStatus.BAD_REQUEST, "40005", "지원되지 않는 HTTP method 요청입니다."),
    INVALID_TOKEN_HEADER_ERROR(HttpStatus.BAD_REQUEST, "40006", "토큰 헤더값의 형식이 잘못되었습니다."),
    INVALID_CODE_HEADER_ERROR(HttpStatus.BAD_REQUEST, "40007", "code 헤더값의 형식이 잘못되었습니다."),
    INVALID_SOCIAL_TYPE_ERROR(HttpStatus.BAD_REQUEST, "40008", "유효하지 않은 Social Type입니다."),
    BEARER_LOST_ERROR(HttpStatus.BAD_REQUEST, "40009", "요청한 토큰이 Bearer 토큰이 아닙니다."),
    INVALID_NICKNAME_ERROR(HttpStatus.BAD_REQUEST, "40010", "닉네임이 조건을 만족하지 않습니다."),
    INVALID_PHONE_NUMBER_ERROR(HttpStatus.BAD_REQUEST, "40011", "잘못된 휴대전화 번호 양식입니다."),
    INVALID_VERIFICATION_CODE_ERROR(HttpStatus.BAD_REQUEST, "40012", "인증번호가 올바르지 않습니다."),
    NO_VERIFICATION_REQUEST_HISTORY(HttpStatus.BAD_REQUEST, "40013", "인증 요청 이력이 존재하지 않습니다."),
    INVALID_EMAIL_DOMAIN_ERROR(HttpStatus.BAD_REQUEST, "40014", "대학과 일치하지 않는 메일 도메인입니다."),
    INVALID_UNIV_NAME_ERROR(HttpStatus.BAD_REQUEST, "40015", "서버에 존재하지 않는 대학명입니다."),
    MAP_TO_JSON_ERROR(HttpStatus.BAD_REQUEST, "40016", "Map을 JSON 문자열로 변환하는 중 오류가 발생했습니다."),
    JSON_TO_MAP_ERROR(HttpStatus.BAD_REQUEST, "40017", "JSON 문자열을 Map으로 변환하는 중 오류가 발생했습니다."),
    UNIV_CERT_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "40018", "이미 인증이 완료된 이메일입니다."),
    SAME_MEMBER_APPOINTMENT_ERROR(HttpStatus.BAD_REQUEST, "40019", "자기 자신에게는 약속을 신청할 수 없습니다."),
    NOT_MEMBERS_APPOINTMENT_ERROR(HttpStatus.BAD_REQUEST, "40020", "해당 회원의 약속이 아닙니다."),
    NOT_VALID_OCR_IMAGE(HttpStatus.BAD_REQUEST, "40021", "이미지 인식에 실패했습니다."),
    INVALID_SAME_SENIOR(HttpStatus.BAD_REQUEST, "40022", "이미 약속을 신청한 선배입니다."),
    NOT_PENDING_APPOINTMENT_ERROR(HttpStatus.BAD_REQUEST, "40023", "확정 대기 약속이 아닙니다."),
    NO_RESPONSE_BODY_ERROR(HttpStatus.BAD_REQUEST, "40024", "Response Body가 존재하지 않습니다."),
    REDIRECT_URI_MISMATCH_ERROR(HttpStatus.BAD_REQUEST, "40025", "잘못된 Redirect Uri입니다."),
    INVALID_USER_TYPE_ERROR(HttpStatus.BAD_REQUEST, "40026", "유효하지 않은 User Type입니다."),
    INVALID_BOTH_TOPICS_PROVIDED(HttpStatus.BAD_REQUEST, "40027", "고민은 선택/작성 중 하나만 골라야 합니다."),
    INVALID_NO_TOPIC_PROVIDED(HttpStatus.BAD_REQUEST, "40028", "고민은 선택/작성 중 하나는 골라야 합니다."),

    // S3 관련 오류
    IMAGE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "40051", "이미지 확장자는 jpg, png, webp만 가능합니다."),
    IMAGE_SIZE_ERROR(HttpStatus.BAD_REQUEST, "40052", "이미지 사이즈는 5MB를 넘을 수 없습니다."),
    S3_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "40053", "S3 이미지 업로드에 실패했습니다."),

    // 인증 관련 오류
    EMPTY_PRINCIPAL_ERROR(HttpStatus.BAD_REQUEST, "40076", "Principal 객체가 없습니다. (null)"),

    /**
     * 401 UNAUTHORIZED
     */
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "40101", "유효하지 않는 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "40102", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "40103", "지원하지 않는 JWT 토큰입니다."),
    EMPTY_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "40104", "JWT 토큰이 존재하지 않습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "40105", "잘못된 JWT 서명입니다."),
    UNKNOWN_JWT_ERROR(HttpStatus.UNAUTHORIZED, "40106", "알 수 없는 JWT 토큰 오류가 발생했습니다."),

    INVALID_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "40107", "유효하지 않은 소셜 액세스 토큰입니다."),
    EXPIRED_AUTHENTICATION_CODE(HttpStatus.UNAUTHORIZED, "40108", "인가 코드가 만료되었습니다."),
    UN_LOGIN_ERROR(HttpStatus.UNAUTHORIZED, "40109", "로그인 후 진행해주세요."),
    NOT_AUTHORIZATION_ACCEPT(HttpStatus.UNAUTHORIZED, "40110", "약속을 수락할 권한이 없습니다."),
    NOT_AUTHORIZATION_REJECT(HttpStatus.UNAUTHORIZED, "40111", "약속을 거절할 권한이 없습니다."),

    /**
     * 404 NOT FOUND
     */
    NOT_FOUND_MEMBER_ERROR(HttpStatus.NOT_FOUND, "40401", "존재하지 않는 회원입니다."),
    NOT_FOUND_REFRESH_TOKEN_ERROR(HttpStatus.NOT_FOUND, "40402", "존재하지 않는 리프레시 토큰입니다."),
    NOT_FOUND_CREDENTIALS_JSON_ERROR(HttpStatus.NOT_FOUND, "40403", "구글미트 Credentials Json 파일을 찾을 수 없습니다."),
    NOT_FOUND_SENIOR_BY_MEMBER(HttpStatus.NOT_FOUND, "40404", "해당 ID를 가진 멤버와 매핑된 선배를 찾을 수 없습니다."),
    NOT_FOUND_SENIOR_ERROR(HttpStatus.NOT_FOUND, "40405", "존재하지 않는 선배입니다."),
    NOT_FOUND_APPOINTMENT_ERROR(HttpStatus.NOT_FOUND, "40406", "존재하지 않는 약속입니다."),
    NOT_FOUND_GOOGLE_MEET_LINK_ERROR(HttpStatus.NOT_FOUND, "40407", "구글 미트 링크가 존재하지 않는 약속입니다."),
    NOT_FOUND_UNIV_EMAIL_DOMAIN_ERROR(HttpStatus.NOT_FOUND, "40408", "해당 대학교에 해당하는 메일 도메인이 없습니다."),
    NOT_FOUND_UNIV_NAME_ERROR(HttpStatus.NOT_FOUND, "40409", "DB에 존재하지 않는 대학명입니다."),

    /**
     * 409 CONFLICT
     */
    NICKNAME_DUP_ERROR(HttpStatus.CONFLICT, "40901", "이미 사용 중인 닉네임입니다."),
    PHONE_NUMBER_DUP_ERROR(HttpStatus.CONFLICT, "40902", "이미 사용 중인 휴대전화 번호입니다."),

    /**
     * 500 INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50001", "알 수 없는 서버 에러가 발생했습니다."),
    GET_UPLOAD_PRESIGNED_URL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50002", "업로드를 위한 Presigned URL 획득에 실패했습니다."),
    GET_GOOGLE_MEET_URL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50003", "구글미트 URL 획득에 실패했습니다."),
    GET_GOOGLE_AUTHORIZER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50004", "구글 인증유저 획득에 실패했습니다."),
    INTERNAL_FEIGN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50005", "FEIGN 에러가 발생했습니다."),
    SMTP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50006", "메일 발송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
