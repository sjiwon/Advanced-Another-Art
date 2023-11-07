package com.sjiwon.anotherart.art.utils.search;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PagingConstants {
    int SLICE_PER_PAGE = 8;
    int RANGE_PER_PAGE = 10;

    static Pageable getPageable(final int page) {
        return PageRequest.of(page, SLICE_PER_PAGE);
    }
}
