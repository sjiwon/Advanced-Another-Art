package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.config.DatabaseCleanerEachCallbackExtension;
import com.sjiwon.anotherart.common.config.MySqlTestContainersExtension;
import com.sjiwon.anotherart.common.config.RedisTestContainersExtension;
import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Password;
import com.sjiwon.anotherart.member.domain.model.PointRecord;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.repository.PointRecordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static com.sjiwon.anotherart.member.domain.model.PointType.CHARGE;

@SpringBootTest
@ExtendWith({
        DatabaseCleanerEachCallbackExtension.class,
        MySqlTestContainersExtension.class,
        RedisTestContainersExtension.class
})
public abstract class ConcurrencyTest {
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

    protected void createMembers(final int threadCount) {
        final List<Member> members = new ArrayList<>();
        final List<PointRecord> pointRecords = new ArrayList<>();

        for (int i = 1; i <= threadCount; i++) {
            final Member member = createMember(i);
            increaseMemberPoint(member);
            members.add(member);
            pointRecords.add(PointRecord.addPointRecord(member, CHARGE, 100_000_000));
        }

        memberRepository.saveAll(members);
        pointRecordRepository.saveAll(pointRecords);
        members.forEach(member -> memberIds.add(member.getId()));
    }

    private Member createMember(final int index) {
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
        final String first = "010";
        final String second = String.valueOf((int) (Math.random() * 9000 + 1000));
        final String third = String.valueOf((int) (Math.random() * 9000 + 1000));

        return first + second + third;
    }

    private void increaseMemberPoint(final Member member) {
        ReflectionTestUtils.setField(member.getPoint(), "totalPoint", 100_000_000);
        ReflectionTestUtils.setField(member.getPoint(), "availablePoint", 100_000_000);
    }

    @AfterEach
    void clearDatabase() {
        memberIds.clear();
    }
}
