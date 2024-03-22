package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.repository.HashtagRepository;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRecordRepository;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.file.application.adapter.FileUploader;
import com.sjiwon.anotherart.like.domain.repository.LikeRepository;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.point.domain.repository.PointRecordRepository;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import com.sjiwon.anotherart.token.domain.repository.TokenRepository;
import org.junit.jupiter.api.Tag;

import static org.mockito.Mockito.mock;

@Tag("UseCase")
@ExecuteParallel
public abstract class UnitTest {
    protected final MemberRepository memberRepository = mock(MemberRepository.class);
    protected final ArtRepository artRepository = mock(ArtRepository.class);
    protected final HashtagRepository hashtagRepository = mock(HashtagRepository.class);
    protected final LikeRepository likeRepository = mock(LikeRepository.class);
    protected final AuctionRepository auctionRepository = mock(AuctionRepository.class);
    protected final AuctionRecordRepository auctionRecordRepository = mock(AuctionRecordRepository.class);
    protected final PointRecordRepository pointRecordRepository = mock(PointRecordRepository.class);
    protected final PurchaseRepository purchaseRepository = mock(PurchaseRepository.class);
    protected final TokenRepository tokenRepository = mock(TokenRepository.class);
    protected final FileUploader fileUploader = mock(FileUploader.class);
}
