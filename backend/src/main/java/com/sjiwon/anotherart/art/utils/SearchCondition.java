package com.sjiwon.anotherart.art.utils;

import com.sjiwon.anotherart.art.domain.ArtType;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SearchCondition {
    private ArtType artType;
    private SortType sortType;
    private String givenText;

    @Getter
    public enum SortType {
        DATE_ASC("date"),
        DATE_DESC("rdate"),
        PRICE_ASC("price"),
        PRICE_DESC("rprice"),
        LIKE_ASC("like"),
        LIKE_DESC("rlike"),
        BID_COUNT_ASC("count"),
        BID_COUNT_DESC("rcount");

        private static final List<String> DEFAULT_SORT_TYPES = List.of(
                "date", "rdate",
                "price", "rprice",
                "like", "rlike",
                "count", "rcount"
        );
        private final String value;

        SortType(String value) {
            this.value = value;
        }

        public static boolean isNotSupportedSortType(String sortType) {
            return !DEFAULT_SORT_TYPES.contains(sortType);
        }

        public static SortType convertSortType(String input) {
            return switch (input) {
                case "date" -> SortType.DATE_ASC; // 등록 날짜 오름차순 (과거에 등록된 순)
                case "rdate" -> SortType.DATE_DESC; // 등록 날짜 내림차순 (최근에 등록된 순)
                case "price" -> SortType.PRICE_ASC; // 가격 오름차순 (낮은 가격 순)
                case "rprice" -> SortType.PRICE_DESC; // 가격 내림차순 (높은 가격 순)
                case "like" -> SortType.LIKE_ASC;// 좋아요 오름차순 (좋아요 횟수 적은 순)
                case "rlike" -> SortType.LIKE_DESC; // 좋아요 내림차순 (좋아요 횟수 많은 순)
                case "count" -> SortType.BID_COUNT_ASC; // [경매 작품] - 비드 횟수 오름차순 (비드 횟수 적은 순)
                default -> SortType.BID_COUNT_DESC; // [경매 작품] - 비드 횟수 내림차순 (비드 횟수 많은 순)
            };
        }
    }
}
