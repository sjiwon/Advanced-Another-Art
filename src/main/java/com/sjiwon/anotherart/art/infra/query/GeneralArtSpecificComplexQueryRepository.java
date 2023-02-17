package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.art.utils.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeneralArtSpecificComplexQueryRepository {
    Page<BasicGeneralArt> findGeneralArtListByKeyword(SearchCondition condition, Pageable pageRequest);
    Page<BasicGeneralArt> findGeneralArtListByHashtag(SearchCondition condition, Pageable pageRequest);
}
