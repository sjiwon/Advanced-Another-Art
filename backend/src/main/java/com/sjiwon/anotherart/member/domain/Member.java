package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.member.domain.point.PointRecord;
import com.sjiwon.anotherart.member.domain.point.PointType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static com.sjiwon.anotherart.member.domain.Role.USER;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Embedded
    private Nickname nickname;

    @Column(name = "login_id", nullable = false, unique = true, updatable = false)
    private String loginId;

    @Embedded
    private Password password;

    @Column(name = "school", nullable = false, updatable = false)
    private String school;

    @Column(name = "phone", nullable = false, unique = true, updatable = false)
    private String phone;

    @Embedded
    private Email email;

    @Embedded
    private Address address;

    @Embedded
    private Point point;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    private Member(String name, Nickname nickname, String loginId, Password password,
                   String school, String phone, Email email, Address address) {
        this.name = name;
        this.nickname = nickname;
        this.loginId = loginId;
        this.password = password;
        this.school = school;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.point = Point.init();
        this.role = USER;
    }

    public static Member createMember(String name, Nickname nickname, String loginId, Password password,
                                      String school, String phone, Email email, Address address) {
        return new Member(name, nickname, loginId, password, school, phone, email, address);
    }

    public void addPointRecords(PointType type, int amount) {
        this.point.addPointRecords(this, type, amount);
    }

    public void becomeTopBidder(int point) {
        this.point = this.point.decreaseAvailablePoint(point);
    }

    public void withdrawFromAuctionAsTopBidder(int point) {
        this.point = this.point.increaseAvailablePoint(point);
    }

    public void changeNickname(String nickname) {
        this.nickname = this.nickname.update(nickname);
    }

    public void changePassword(String password, PasswordEncoder encoder) {
        this.password = this.password.update(password, encoder);
    }

    public void changeAddress(int postcode, String defaultAddress, String detailAddress) {
        this.address = this.address.update(postcode, defaultAddress, detailAddress);
    }

    public boolean isSameMember(Long compareId) {
        return Objects.equals(this.id, compareId);
    }

    // Add Getter
    public String getNicknameValue() {
        return nickname.getValue();
    }

    public String getPasswordValue() {
        return password.getValue();
    }

    public String getEmailValue() {
        return email.getValue();
    }

    public List<PointRecord> getPointRecords() {
        return point.getPointRecords();
    }

    public int getTotalPoint() {
        return point.getTotalPoint();
    }

    public int getAvailablePoint() {
        return point.getAvailablePoint();
    }
}
