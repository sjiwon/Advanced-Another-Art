package com.sjiwon.anotherart.art.utils.search;

import com.sjiwon.anotherart.art.exception.ArtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.SORT_TYPE_NOT_PROVIED;

@Getter
@RequiredArgsConstructor
public enum SearchSortType {
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

    public static boolean isNotSupportedSortType(final String value) {
        return Arrays.stream(values())
                .noneMatch(sortType -> sortType.value.equals(value));
    }

    public static SearchSortType from(final String value) {
        return Arrays.stream(values())
                .filter(sortType -> sortType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new ArtException(SORT_TYPE_NOT_PROVIED));
    }
}
