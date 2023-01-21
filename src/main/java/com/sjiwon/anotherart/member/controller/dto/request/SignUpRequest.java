package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.domain.Address;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.Password;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "이름은 필수입니다")
    @ApiModelProperty(value = "사용자 이름", example = "test", required = true)
    private String name;

    @NotBlank(message = "닉네임은 필수입니다")
    @ApiModelProperty(value = "사용자 닉네임 (Unique)", example = "test", required = true)
    private String nickname;

    @NotBlank(message = "아이디는 필수입니다")
    @ApiModelProperty(value = "로그인 아이디 (Unique)", example = "test", required = true)
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @ApiModelProperty(value = "로그인 비밀번호", example = "abcABC123!@#", required = true)
    private String password;

    @NotBlank(message = "학교 이름은 필수입니다")
    @ApiModelProperty(value = "사용자 재학중인 학교명", example = "경기대학교", required = true)
    private String school;

    @NotNull(message = "우편번호는 필수입니다")
    @ApiModelProperty(value = "사용자 주소 우편번호", example = "12345", required = true)
    private Integer postcode;

    @NotBlank(message = "주소는 필수입니다")
    @ApiModelProperty(value = "사용자 주소", example = "경기 성남시 분당구 정자일로 95", required = true)
    private String defaultAddress;

    @NotBlank(message = "상세주소는 필수입니다")
    @ApiModelProperty(value = "사용자 상세 주소", example = "네이버", required = true)
    private String detailAddress;

    @NotBlank(message = "전화번호는 필수입니다")
    @ApiModelProperty(value = "사용자 전화번호 (Unique)", example = "01012345678", required = true)
    private String phone;

    @NotBlank(message = "이메일은 필수입니다")
    @ApiModelProperty(value = "사용자 이메일 (Unique)", example = "test@gmail.com", required = true)
    private String email;

    public Member toMemberEntity() {
        return Member.builder()
                .name(name)
                .nickname(nickname)
                .loginId(loginId)
                .password(Password.encrypt(password, PasswordEncoderFactories.createDelegatingPasswordEncoder()))
                .school(school)
                .address(Address.of(postcode, defaultAddress, detailAddress))
                .phone(phone)
                .email(Email.from(email))
                .build();
    }
}
