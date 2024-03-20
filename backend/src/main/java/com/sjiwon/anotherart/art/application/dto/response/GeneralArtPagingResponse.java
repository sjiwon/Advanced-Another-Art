package com.sjiwon.anotherart.art.application.dto.response;

import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.global.query.Pagination;

import java.util.List;

public record GeneralArtPagingResponse(
        List<GeneralArt> result,
        Pagination pagination
) {
}
