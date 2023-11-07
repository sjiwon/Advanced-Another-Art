package com.sjiwon.anotherart.member.domain.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.WinningAuctionArts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

// TODO 전체 Command 로직 리팩토링 후 Query 로직 한번에 구현
@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class MemberInformationQueryRepositoryImpl implements MemberInformationQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public MemberInformation fetchInformation(final long memberId) {
        return null;
    }

    @Override
    public List<MemberPointRecord> fetchPointRecords(final long memberId) {
        return null;
    }

    @Override
    public List<WinningAuctionArts> fetchWinningAuctionArts(final long memberId) {
        return null;
    }

    @Override
    public List<SoldArts> fetchSoldArtsByType(final long memberId, final ArtType type) {
        return null;
    }

    @Override
    public List<PurchaseArts> fetchPurchaseArtsByType(final long memberId, final ArtType type) {
        return null;
    }
}
