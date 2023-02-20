package com.sjiwon.anotherart.member.service.dto.response;

import com.sjiwon.anotherart.member.domain.Address;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private String name;
    private String nickname;
    private String loginId;
    private String email;
    private String school;
    private String phone;
    private Address address;
    private int availablePoint; // 사용 가능 포인트
    private int totalPoint; // 전체 보유 포인트

    @Builder
    public UserProfile(Member member, int totalPoint) {
        this.id = member.getId();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.loginId = member.getLoginId();
        this.email = member.getEmailValue();
        this.school = member.getSchool();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.availablePoint = member.getAvailablePoint();
        this.totalPoint = totalPoint;
    }
}
