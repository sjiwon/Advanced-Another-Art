package com.sjiwon.anotherart.art.exception;

public class ArtRequestValidationMessage {
    public static final String ART_HASHTAG_LIST_MIN = "작품은 최소 %d개의 해시태그를 가져야 합니다.";
    public static final String ART_HASHTAG_LIST_MAX = "작품은 최대 %d개의 해시태그를 가질 수 있습니다.";

    public static class Registration {
        public static final String ART_TYPE = "작품 타입(경매/일반)은 필수입니다.";
        public static final String ART_NAME = "작품 작품명은 필수입니다.";
        public static final String ART_DESCRIPTION = "작품 설명은 필수입니다.";
        public static final String ART_PRICE = "작품 가격은 필수입니다.";
        public static final String ART_PRICE_MIN = "작품 가격은 최소 1000원 이상이여야 합니다.";
        public static final String ART_FILE_EMPTY = "작품 이미지가 업로드되지 않았습니다.";
        public static final String ART_FILE_FORMAT = "png/jpg/jpeg 형식의 이미지만 업로드 가능합니다.";
        public static final String AUCTION_ART_START_DATE = "경매 시작 날짜는 현재 시간 이후여야 합니다.";
    }

    public static class ChangeArtInfo {
        public static final String CHANGE_DESCRIPTION = "변경할 작품 설명은 필수입니다.";
    }
}
