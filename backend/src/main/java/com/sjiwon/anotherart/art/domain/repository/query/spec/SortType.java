package com.sjiwon.anotherart.art.domain.repository.query.spec;

import com.sjiwon.anotherart.art.exception.ArtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.SORT_TYPE_NOT_PROVIED;

@Getter
@RequiredArgsConstructor
public enum SortType {
    /**
     * 등록 날짜 ASC | DESC
     */
    DATE_ASC("date"),
    DATE_DESC("rdate"),

    /**
     * 가격 ASC | DESC <br>
     * -> 일반 작품 = 가격 <br>
     * -> 경매 작품 = 입찰 가격
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

    public static SortType from(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new ArtException(SORT_TYPE_NOT_PROVIED));
    }
}
