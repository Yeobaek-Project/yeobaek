package com.ssafy.yeobaek.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    // 성공
    SUCCESS("S0000", "정상 처리", HttpStatus.OK),
    CREATED("S0000", "정상 처리", HttpStatus.CREATED),

    // 공통 에러 (400 Bad Request)
    MISSING_REQUIRED_FIELD("S4001", "필수 입력값이 누락되었습니다", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_VALUE("S4002", "입력값이 유효하지 않습니다", HttpStatus.BAD_REQUEST),

    // 공통 에러 (401 Unauthorized)
    UNAUTHORIZED("S4011", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING("S4012", "토큰이 누락되었습니다", HttpStatus.UNAUTHORIZED),

    // 공통 에러 (403 Forbidden)
    FORBIDDEN("S4031", "해당 요청에 대한 권한이 없습니다", HttpStatus.FORBIDDEN),

    // 회원 관련 에러 (400 Bad Request)
    EMAIL_ALREADY_EXISTS("AU4001", "이미 사용 중인 이메일입니다", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("AU4002", "비밀번호는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("AU4003", "비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("AU4004", "이메일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_USER_TYPE("AU4006", "유효하지 않은 회원 유형입니다", HttpStatus.BAD_REQUEST),
    INVALID_NICKNAME_LENGTH("AU4007", "닉네임은 2~15자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    INVALID_BIRTH_DATE_FORMAT("AU4008", "생년월일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_GENDER("AU4009", "유효하지 않은 성별입니다", HttpStatus.BAD_REQUEST),

    // 회원 관련 에러 (401 Unauthorized)
    INVALID_CREDENTIALS("AU4011", "비밀번호가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("AU4012", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("AU4013", "만료된 토큰입니다", HttpStatus.UNAUTHORIZED),

    // 회원 관련 에러 (404 Not Found)
    USER_NOT_FOUND("AU4041", "존재하지 않는 회원입니다", HttpStatus.NOT_FOUND),

    // 사용자 프로필 관련 에러 (400 Bad Request)
    INVALID_PROFILE_NICKNAME_LENGTH("US4002", "닉네임은 2~15자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    INVALID_INTRODUCTION_LENGTH("US4003", "작가 소개는 최대 250자까지 입력 가능합니다", HttpStatus.BAD_REQUEST),
    INVALID_PROFILE_BIRTH_DATE("US4004", "생년월일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_PROFILE_GENDER("US4005", "유효하지 않은 성별입니다", HttpStatus.BAD_REQUEST),

    // 사용자 프로필 관련 에러 (401 Unauthorized)
    INCORRECT_CURRENT_PASSWORD("US4011", "현재 패스워드가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),

    // 사용자 프로필 관련 에러 (404 Not Found)
    PROFILE_USER_NOT_FOUND("US4041", "존재하지 않는 회원입니다", HttpStatus.NOT_FOUND),

    // 작품 관리 관련 에러 (400 Bad Request)
    ARTWORK_INVALID_IMAGES_LENGTH("AW4001", "작품 이미지는 최소 1장 최대 10장까지 등록 가능합니다", HttpStatus.BAD_REQUEST),
    ARTWORK_INVALID_TITLE_LENGTH("AW4003", "작품명은 1~100자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    ARTWORK_INVALID_DESCRIPTION_LENGTH("AW4004", "작품 설명은 최대 500자까지 입력 가능합니다", HttpStatus.BAD_REQUEST),
    ARTWORK_INVALID_CATEGORY("AW4006", "유효하지 않은 카테고리입니다", HttpStatus.BAD_REQUEST),
    ARTWORK_SIZE_FILTER_FORBIDDEN("AW4007", "작품 규격 필터는 카페 사장만 사용할 수 있습니다", HttpStatus.BAD_REQUEST),

    // 작품 관리 관련 에러 (403 Forbidden)
    ARTWORK_FORBIDDEN("AW4032", "본인의 작품만 수정/삭제할 수 있습니다", HttpStatus.FORBIDDEN),

    // 작품 관리 관련 에러 (404 Not Found)
    ARTWORK_NOT_FOUND("AW4041", "존재하지 않는 작품입니다", HttpStatus.NOT_FOUND),

    // 작품 관리 관련 에러 (409 Conflict)
    ARTWORK_EXHIBITING_SIZE_CHANGE("AW4091", "전시 중인 작품은 규격을 변경할 수 없습니다", HttpStatus.CONFLICT),
    ARTWORK_CANNOT_DELETE_EXHIBITING("AW4092", "전시 중이거나 전시 예정인 작품은 삭제할 수 없습니다", HttpStatus.CONFLICT),

    // 카페 관련 에러 (400 Bad Request)
    CAFE_INVALID_IMAGES_LENGTH("CF4001", "카페 이미지는 최소 3장 최대 10장까지 등록 가능합니다", HttpStatus.BAD_REQUEST),
    CAFE_WALL_INVALID_NAME_LENGTH("CF4002", "카페 벽 이름은 2~15자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    SLOT_INVALID_REQUIREMENT_LENGTH("CF4003", "기타 요청사항은 500자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    CAFE_INVALID_NAME_LENGTH("CF4004", "매장명은 2~15자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    CAFE_INVALID_DESCRIPTION_LENGTH("CF4005", "카페 소개는 최대 500자까지 입력 가능합니다", HttpStatus.BAD_REQUEST),
    SLOT_INVALID_COMMISSION_RATE("CF4006", "수수료율은 0~15 사이여야 합니다", HttpStatus.BAD_REQUEST),
    CAFE_INVALID_HOURS_FORMAT("CF4007", "영업시간 형식이 올바르지 않습니다 (HH:mm)", HttpStatus.BAD_REQUEST),
    CAFE_LATITUDE_LONGITUDE_REQUIRED("CF4008", "거리순 정렬 시 위도/경도가 필요합니다", HttpStatus.BAD_REQUEST),
    CAFE_INVALID_ADDRESS("CF4009", "좌표로 변환할 수 없는 주소입니다", HttpStatus.BAD_REQUEST),

    // 카페 관련 에러 (403 Forbidden)
    CAFE_FORBIDDEN_UPDATE_DELETE("CF4032", "본인의 카페만 수정/삭제할 수 있습니다", HttpStatus.FORBIDDEN),
    SLOT_FILTER_FORBIDDEN("CF4033", "슬롯 규격 필터는 작가만 사용할 수 있습니다", HttpStatus.FORBIDDEN),
    CAFE_COMMISSION_FILTER_FORBIDDEN("CF4034", "수수료율 필터는 작가만 사용할 수 있습니다", HttpStatus.FORBIDDEN),

    // 카페 관련 에러 (404 Not Found)
    CAFE_NOT_FOUND("CF4041", "존재하지 않는 카페입니다", HttpStatus.NOT_FOUND),
    SLOT_NOT_FOUND("CF4042", "존재하지 않는 슬롯입니다", HttpStatus.NOT_FOUND),
    CAFE_CATEGORY_NOT_FOUND("CF4043", "존재하지 않는 카테고리입니다", HttpStatus.NOT_FOUND),
    SLOT_NOT_FOUND_IN_CAFE("CF4044", "해당 카페에 존재하지 않는 슬롯입니다", HttpStatus.NOT_FOUND),

    // 카페 관련 에러 (409 Conflict)
    CAFE_ALREADY_EXISTS("CF4091", "이미 등록된 카페가 있습니다", HttpStatus.CONFLICT),
    SLOT_EXHIBITING_SIZE_CHANGE("CF4092", "전시 중인 슬롯은 규격을 변경할 수 없습니다", HttpStatus.CONFLICT),
    SLOT_CANNOT_DELETE_EXHIBITING("CF4093", "전시 중이거나 전시 예정인 슬롯은 삭제할 수 없습니다", HttpStatus.CONFLICT),
    WALL_ALREADY_EXISTS("CF4094", "이미 등록된 카페 공간이 있습니다", HttpStatus.CONFLICT),

    // Storage/S3 관련 에러 (400 Bad Request)
    INVALID_STORAGE_FOLDER("ST4001", "유효하지 않은 저장 폴더입니다", HttpStatus.BAD_REQUEST),
    INVALID_FILE_COUNT("ST4002", "파일 개수는 1개 이상 10개 이하여야 합니다", HttpStatus.BAD_REQUEST),

    // 채팅 관련 에러 (400 Bad Request)
    MESSAGE_SELF_SEND_NOT_ALLOWED("MS4001", "자신에게 메시지를 보낼 수 없습니다", HttpStatus.BAD_REQUEST),
    MESSAGE_INVALID_SORT("MS4002", "유효하지 않은 정렬 기준입니다", HttpStatus.BAD_REQUEST),
    MESSAGE_INVALID_TYPE("MS4003", "유효하지 않은 메시지 유형 기준입니다", HttpStatus.BAD_REQUEST),

    // 채팅 관련 에러 (403 Bad Forbidden)
    MESSAGE_ROOM_FORBIDDEN("MS4031", "자신이 속한 채팅방만 조회할 수 있습니다", HttpStatus.FORBIDDEN),

    // 채팅 관련 에러 (404 Not Found)
    MESSAGE_ROOM_NOT_FOUND("MS4041", "존재하지 않는 채팅방입니다", HttpStatus.NOT_FOUND),

    // 채팅 관련 에러 (500 Internal Server Error)
    MESSAGE_SEND_FAILED("MS5001", "메시지 전송에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 리뷰 관련 에러 (400 Bad Request)
    REVIEW_INVALID_RATING("RV4001", "평점은 1~5 사이여야 합니다", HttpStatus.BAD_REQUEST),
    REVIEW_INVALID_COMMENT_LENGTH("RV4002", "후기는 최대 100자까지 입력 가능합니다", HttpStatus.BAD_REQUEST),

    // 리뷰 관련 에러 (409 Conflict)
    REVIEW_ALREADY_EXISTS("RV4091", "이미 작성한 평가입니다", HttpStatus.CONFLICT),

    // 전시 관련 에러 (400 Bad Request)
    EXHIBITION_INVALID_COMMISSION_RATE("EX4001", "수수료율은 0~15 사이여야 합니다", HttpStatus.BAD_REQUEST),
    EXHIBITION_INVALID_REQUIREMENTS_LENGTH("EX4002", "추가 조건은 최대 500자까지 입력 가능합니다", HttpStatus.BAD_REQUEST),
    EXHIBITION_INVALID_EXHIBITION_DATE("EX4003", "전시 종료일은 시작일보다 늦어야 합니다", HttpStatus.BAD_REQUEST),
    EXHIBITION_DATE_PAST("EX4004", "전시 시작일은 오늘 이후여야 합니다", HttpStatus.BAD_REQUEST),
    EXHIBITION_NOT_PENDING("EX4005", "대기 중인 전시 제안만 동의/거절할 수 있습니다", HttpStatus.BAD_REQUEST),
    EXHIBITION_ALREADY_AGREED("EX4006", "이미 동의한 전시 제안입니다", HttpStatus.BAD_REQUEST),

    // 전시 관련 에러 (403 Forbidden)
    EXHIBITION_FORBIDDEN("EX4031", "해당 전시에 대한 권한이 없습니다", HttpStatus.FORBIDDEN),

    // 전시 관련 에러 (404 Not Found)
    EXHIBITION_NOT_FOUND("EX4041", "존재하지 않는 전시입니다", HttpStatus.NOT_FOUND),

    // 전시 관련 에러 (409 Conflict)
    SLOT_ALREADY_RESERVED("EX4091", "해당 기간에 이미 예약된 슬롯입니다", HttpStatus.CONFLICT),
    ARTWORK_ALREADY_RESERVED("EX4092", "해당 기간에 이미 다른 전시가 예정된 작품입니다", HttpStatus.CONFLICT),

    // 서버 에러 (500 Internal Server Error)
    INTERNAL_SERVER_ERROR("S5000", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    GEOCODING_API_ERROR("S5001", "지오코더 API 서버에 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
