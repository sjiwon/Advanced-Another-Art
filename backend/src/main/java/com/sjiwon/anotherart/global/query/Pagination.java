package com.sjiwon.anotherart.global.query;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sjiwon.anotherart.global.query.PageCreator.RANGE_PER_PAGE;
import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class Pagination {
    private long totalElements; // 전체 작품 데이터 개수
    private int totalPages; // 전체 페이지 개수
    private int currentPage; // 현재 페이지
    private int rangeStartNumber; // 현재 범위 시작 번호
    private int rangeEndNumber; // 현재 범위 마지막 번호
    private boolean prevExists; // 이전 range 존재 여부
    private boolean nextExists; // 다음 range 존재 여부

    private Pagination(final long totalElements, final int totalPages, final int currentPage) {
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

        this.prevExists = this.rangeStartNumber > RANGE_PER_PAGE;
        this.nextExists = this.rangeEndNumber + 1 <= totalPages;
    }

    public static Pagination of(final long totalElements, final int totalPages, final int currentPage) {
        return new Pagination(totalElements, totalPages, currentPage);
    }

    public static Pagination empty(final int currentPage) {
        return new Pagination(0L, 0, currentPage);
    }
}
