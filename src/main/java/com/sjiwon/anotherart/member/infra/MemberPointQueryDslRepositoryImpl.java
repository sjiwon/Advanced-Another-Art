package com.sjiwon.anotherart.member.infra;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjiwon.anotherart.member.domain.point.QPointDetail.pointDetail;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPointQueryDslRepositoryImpl implements MemberPointQueryDslRepository{
    private final JPAQueryFactory query;

    @Override
    public Integer getTotalPointsByMemberId(Long memberId) {
        List<PointDetail> details = query
                .select(pointDetail)
                .from(pointDetail)
                .where(memberIdEq(memberId))
                .fetch();

        int totalPoints = 0;
        for (PointDetail detail : details) {
            if (detail.isPointIncreaseType()) {
                totalPoints += detail.getAmount();
            } else {
                totalPoints -= detail.getAmount();
            }
        }
        return totalPoints;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return (memberId != null) ? pointDetail.member.id.eq(memberId) : null;
    }
}