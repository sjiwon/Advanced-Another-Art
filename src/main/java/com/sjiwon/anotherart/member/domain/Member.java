package com.sjiwon.anotherart.member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "nickname", nullable = false, unique = true, length = 100)
    private String nickname;

    @Column(name = "login_id", nullable = false, unique = true, updatable = false, length = 50)
    private String loginId;

    @Embedded
    private Password password;

    @Column(name = "school", nullable = false, updatable = false, length = 50)
    private String school;

    @Embedded
    private Address address;

    @Column(name = "phone", nullable = false, unique = true, updatable = false, length = 11)
    private String phone;

    @Embedded
    private Email email;

    @Embedded
    private AvailablePoint availablePoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, updatable = false, length = 20)
    private Role role;

    @Builder
    private Member(String name, String nickname, String loginId, Password password, String school, Address address, String phone, Email email) {
        this.name = name;
        this.nickname = nickname;
        this.loginId = loginId;
        this.password = password;
        this.school = school;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.availablePoint = AvailablePoint.from(0);
        this.role = Role.USER;
    }

    public static Member createMember(String name, String nickname, String loginId, Password password, String school, Address address, String phone, Email email) {
        return new Member(name, nickname, loginId, password, school, address, phone, email);
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password, PasswordEncoder encoder) {
        this.password = this.password.update(password, encoder);
    }

    public void changeAddress(int postcode, String defaultAddress, String detailAddress) {
        this.address = this.address.update(postcode, defaultAddress, detailAddress);
    }

    public void updateAvailablePoint(int point) {
        this.availablePoint = this.availablePoint.update(point);
    }
}
