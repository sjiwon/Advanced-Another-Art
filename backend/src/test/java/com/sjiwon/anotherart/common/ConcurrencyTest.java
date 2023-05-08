package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.member.domain.*;
import com.sjiwon.anotherart.member.domain.point.PointRecord;
import com.sjiwon.anotherart.member.domain.point.PointRecordRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;

@SpringBootTest
public class ConcurrencyTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PointRecordRepository pointRecordRepository;

    @Autowired
    protected ArtRepository artRepository;

    @Autowired
    protected AuctionRepository auctionRepository;

    protected final List<Long> memberIds = new ArrayList<>();

    protected void createMembers(int threadCount) {
        List<Member> members = new ArrayList<>();
        List<PointRecord> pointRecords = new ArrayList<>();

        for (int i = 1; i <= threadCount; i++) {
            Member member = createMember(i);
            increaseMemberPoint(member);
            members.add(member);
            pointRecords.add(PointRecord.addPointRecord(member, CHARGE, 100_000_000));
        }

        memberRepository.saveAll(members);
        pointRecordRepository.saveAll(pointRecords);
        members.forEach(member -> memberIds.add(member.getId()));
    }

    private Member createMember(int index) {
        return Member.createMember(
                "name" + index,
                Nickname.from("nick" + index),
                "loginid" + index,
                Password.encrypt("abcABC123!@#", PasswordEncoderUtils.getEncoder()),
                "경기대학교",
                generateRandomPhoneNumber(),
                Email.from("test" + index + "@gmail.com"),
                Address.of(12345, "기본 주소", "상세 주소")
        );
    }

    private String generateRandomPhoneNumber() {
        String first = "010";
        String second = String.valueOf((int) (Math.random() * 9000 + 1000));
        String third = String.valueOf((int) (Math.random() * 9000 + 1000));

        return first + second + third;
    }

    private void increaseMemberPoint(Member member) {
        ReflectionTestUtils.setField(member.getPoint(), "totalPoint", 100_000_000);
        ReflectionTestUtils.setField(member.getPoint(), "availablePoint", 100_000_000);
    }

    @AfterEach
    void clearDatabase() {
        databaseCleaner.cleanUpDatabase();
        memberIds.clear();
    }
}
