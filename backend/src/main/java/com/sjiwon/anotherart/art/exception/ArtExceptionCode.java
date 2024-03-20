package com.sjiwon.anotherart.art.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum ArtExceptionCode implements BusinessExceptionCode {
    NAME_IS_BLANK(BAD_REQUEST, "ART_001", "작품명은 공백을 허용하지 않습니다."),
    NAME_LENGTH_OUT_OF_RANGE(BAD_REQUEST, "ART_002", "작품명은 최대 20자까지 가능합니다."),
    DESCRIPTION_IS_BLANK(BAD_REQUEST, "ART_003", "작품 설명은 공백을 허용하지 않습니다."),
    DESCRIPTION_LENGTH_OUT_OF_RANGE(BAD_REQUEST, "ART_004", "작품 설명은 최대 1000자까지 가능합니다."),
    HASHTAG_MUST_EXISTS_AT_LEAST_ONE(BAD_REQUEST, "ART_005", "작품 해시태그는 적어도 1개 이상 존재해야 합니다."),
    HASHTAG_MUST_NOT_EXISTS_MORE_THAN_TEN(BAD_REQUEST, "ART_006", "작품 해시태그는 10개 이하로 작성해주세요."),
    ART_NOT_FOUND(NOT_FOUND, "ART_007", "작품 정보가 존재하지 않습니다."),
    DUPLICATE_NAME(CONFLICT, "ART_008", "이미 사용중인 작품명입니다."),
    INVALID_ART_TYPE(BAD_REQUEST, "ART_009", "제공하지 않는 작품 타입입니다."),
    CANNOT_DELETE_SOLD_ART(CONFLICT, "ART_010", "이미 판매된 작품은 삭제할 수 없습니다."),
    CANNOT_DELETE_IF_BID_EXISTS(CONFLICT, "ART_011", "이미 입찰이 진행된 경매 작품은 삭제할 수 없습니다."),
    SORT_TYPE_NOT_PROVIED(BAD_REQUEST, "ART_012", "제공하지 않는 정렬 타입입니다."),
    INVALID_DUPLICATE_RESOURCE(BAD_REQUEST, "ART_013", "중복 체크 대상이 아닙니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
