package com.sjiwon.anotherart.like.domain.model;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "art_like")
public class Like extends BaseEntity<Like> {
    @Column(name = "art_id", nullable = false, updatable = false)
    private Long artId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    public Like(final Art art, final Member member) {
        this.artId = art.getId();
        this.memberId = member.getId();
    }
}
