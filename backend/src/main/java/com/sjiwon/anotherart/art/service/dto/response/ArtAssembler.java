package com.sjiwon.anotherart.art.service.dto.response;

import com.sjiwon.anotherart.art.infra.query.dto.response.ArtDetails;
import com.sjiwon.anotherart.art.utils.search.Pagination;

import java.util.List;

public record ArtAssembler(
        List<ArtDetails> result,
        Pagination pagination
) {
}
