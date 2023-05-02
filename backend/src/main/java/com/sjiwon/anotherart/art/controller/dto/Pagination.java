package com.sjiwon.anotherart.art.controller.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pagination {
    public static final int SLICE_PER_PAGE = 8; // 한 페이지에서 보이는 작품 개수
    public static final int RANGE_PER_PAGE = 10; // 하단 페이징바에서 표시되는 페이지 개수
    private Long totalElements; // 전체 작품 데이터 개수
    private int totalPages; // 전체 페이지 개수
    private int currentPage; // 현재 페이지
    private int rangeStartNumber; // 현재 범위 시작 번호
    private int rangeEndNumber; // 현재 범위 마지막 번호
    private boolean prev; // 이전 range 존재 여부
    private boolean next; // 다음 range 존재 여부

    @Builder
    public Pagination(Long totalElements, int totalPages, int currentPage) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;

        this.rangeStartNumber = RANGE_PER_PAGE * (currentPage / RANGE_PER_PAGE) + 1;
        this.rangeEndNumber = RANGE_PER_PAGE * (currentPage / RANGE_PER_PAGE) + RANGE_PER_PAGE;

        if (currentPage % RANGE_PER_PAGE == 0) {
            rangeStartNumber = currentPage - (RANGE_PER_PAGE - 1);
            rangeEndNumber = currentPage;
        } else if (rangeEndNumber > totalPages) {
            rangeEndNumber = totalPages;
        }

        this.prev = this.rangeStartNumber > RANGE_PER_PAGE;
        this.next = this.rangeEndNumber + 1 <= totalPages;
    }
}
