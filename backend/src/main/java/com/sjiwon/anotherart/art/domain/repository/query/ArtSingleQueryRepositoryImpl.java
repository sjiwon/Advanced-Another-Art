//package com.sjiwon.anotherart.art.domain.repository.query;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.sjiwon.anotherart.art.domain.model.Art;
//import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
//import com.sjiwon.anotherart.art.domain.repository.query.response.GeneralArt;
//import com.sjiwon.anotherart.art.domain.repository.query.response.QAuctionArt;
//import com.sjiwon.anotherart.art.domain.repository.query.response.QGeneralArt;
//import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
//import com.sjiwon.anotherart.member.domain.model.QMember;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//import static com.sjiwon.anotherart.art.domain.model.QArt.art;
//import static com.sjiwon.anotherart.art.domain.model.QHashtag.hashtag;
//import static com.sjiwon.anotherart.auction.domain.model.QAuction.auction;
//import static com.sjiwon.anotherart.auction.domain.model.QAuctionRecord.auctionRecord;
//import static com.sjiwon.anotherart.like.domain.model.QLike.like;
//import static com.sjiwon.anotherart.member.domain.model.QMember.member;
//import static com.sjiwon.anotherart.purchase.domain.model.QPurchase.purchase;
//
//@Repository
//@AnotherArtReadOnlyTransactional
//@RequiredArgsConstructor
//public class ArtSingleQueryRepositoryImpl implements ArtSingleQueryRepository {
//    private static final QMember owner = new QMember("owner");
//    private static final QMember highestBidder = new QMember("highestBidder");
//    private static final QMember buyer = new QMember("buyer");
//
//    private final JPAQueryFactory query;
//
//    @Override
//    public Art.Type getArtType(final Long artId) {
//        return query
//                .select(art.type)
//                .from(art)
//                .where(art.id.eq(artId))
//                .fetchOne();
//    }
//
//    @Override
//    public AuctionArt fetchAuctionArt(final Long artId) {
//        final AuctionArt result = query
//                .select(new QAuctionArt(
//                        auction.id,
//                        auction.highestBidPrice,
//                        auction.period.startDate,
//                        auction.period.endDate,
//                        art.id,
//                        art.name,
//                        art.description,
//                        art.price,
//                        art.status,
//                        art.uploadImage,
//                        art.createdAt,
//                        owner.id,
//                        owner.nickname,
//                        owner.school,
//                        highestBidder.id,
//                        highestBidder.nickname,
//                        highestBidder.school
//                ))
//                .from(art)
//                .innerJoin(member, owner).on(owner.id.eq(art.ownerId))
//                .innerJoin(auction).on(auction.artId.eq(art.id))
//                .leftJoin(member, highestBidder).on(highestBidder.id.eq(auction.highestBidderId))
//                .where(art.id.eq(artId))
//                .fetchOne();
//
//        if (result != null) {
//            result.applyHashtags(getHashtags(artId));
//            result.applyLikeMembers(getLikeMemberIds(artId));
//            result.applyBidCount(getBidCount(artId));
//        }
//
//        return result;
//    }
//
//    @Override
//    public GeneralArt fetchGeneralArt(final Long artId) {
//        final GeneralArt result = query
//                .select(new QGeneralArt(
//                        art.id,
//                        art.name,
//                        art.description,
//                        art.price,
//                        art.status,
//                        art.uploadImage,
//                        art.createdAt,
//                        owner.id,
//                        owner.nickname,
//                        owner.school,
//                        buyer.id,
//                        buyer.nickname,
//                        buyer.school
//                ))
//                .from(art)
//                .innerJoin(member, owner).on(owner.id.eq(art.ownerId))
//                .leftJoin(purchase).on(purchase.artId.eq(art.id))
//                .leftJoin(member, buyer).on(buyer.id.eq(purchase.buyerId))
//                .where(art.id.eq(artId))
//                .fetchOne();
//
//        if (result != null) {
//            result.applyHashtags(getHashtags(artId));
//            result.applyLikeMembers(getLikeMemberIds(artId));
//        }
//
//        return result;
//    }
//
//    private List<String> getHashtags(final Long artId) {
//        return query
//                .select(hashtag.name)
//                .from(hashtag)
//                .where(hashtag.art.id.eq(artId))
//                .fetch();
//    }
//
//    private List<Long> getLikeMemberIds(final Long artId) {
//        return query
//                .select(like.memberId)
//                .from(like)
//                .where(like.artId.eq(artId))
//                .fetch();
//    }
//
//    private int getBidCount(final Long artId) {
//        return query
//                .select(auctionRecord.count())
//                .from(auctionRecord)
//                .innerJoin(auctionRecord.auction, auction)
//                .where(auction.artId.eq(artId))
//                .fetchOne()
//                .intValue();
//    }
//}
