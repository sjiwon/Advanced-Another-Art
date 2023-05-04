package com.sjiwon.anotherart.member.infra.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.QMemberPointRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjiwon.anotherart.member.domain.point.QPointRecord.pointRecord;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInformationQueryRepositoryImpl implements MemberInformationQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<MemberPointRecord> findPointRecordByMemberId(Long memberId) {
        return query
                .select(new QMemberPointRecord(pointRecord.type, pointRecord.amount, pointRecord.createdAt))
                .from(pointRecord)
                .where(pointRecord.member.id.eq(memberId))
                .orderBy(pointRecord.id.desc())
                .fetch();
    }
}
