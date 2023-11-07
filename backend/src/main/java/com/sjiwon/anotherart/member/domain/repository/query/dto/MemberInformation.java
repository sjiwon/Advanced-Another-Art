package com.sjiwon.anotherart.member.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.domain.model.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInformation {
    private final Long id;
    private final String name;
    private final String nickname;
    private final String loginId;
    private final String school;
    private final String phone;
    private final String email;
    private final Address address;
    private final int totalPoint;
    private final int availablePoint;

    @QueryProjection
    public MemberInformation(
            final Long id,
            final String name,
            final Nickname nickname,
            final String loginId,
            final String school,
            final Phone phone,
            final Email email,
            final Address address,
            final Point point
    ) {
        this.id = id;
        this.name = name;
        this.nickname = nickname.getValue();
        this.loginId = loginId;
        this.school = school;
        this.phone = phone.getValue();
        this.email = email.getValue();
        this.address = address;
        this.totalPoint = point.getTotalPoint();
        this.availablePoint = point.getAvailablePoint();
    }
}
