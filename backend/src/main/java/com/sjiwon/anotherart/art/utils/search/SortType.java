package com.sjiwon.anotherart.art.utils.search;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SortType {
    /**
     * 등록 날짜 ASC | DESC
     */
    DATE_ASC("date"),
    DATE_DESC("rdate"),

    /**
     * 가격 ASC | DESC
     */
    PRICE_ASC("price"),
    PRICE_DESC("rprice"),

    /**
     * 좋아요 횟수 ASC | DESC
     */
    LIKE_ASC("like"),
    LIKE_DESC("rlike"),

    /**
     * 입찰 횟수 ASC | DESC
     */
    BID_COUNT_ASC("count"),
    BID_COUNT_DESC("rcount"),
    ;

    private final String value;

    public static SortType from(String value) {
        return Arrays.stream(values())
                .filter(sortType -> sortType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> AnotherArtException.type(ArtErrorCode.SORT_TYPE_NOT_PROVIED));
    }
}
