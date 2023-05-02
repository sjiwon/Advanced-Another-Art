package com.sjiwon.anotherart.art.controller.dto.response;

import com.sjiwon.anotherart.art.controller.dto.Pagination;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PagingArtResponse<T> {
    private int contentSize;
    private T artList;
    private Pagination pagination;
}
