package com.sjiwon.anotherart.member.controller.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DuplicateCheckRequest {
    @NotBlank(message = "중복 체크 타입[nickname / loginId / phone / email]은 필수입니다.")
    private String resource;

    @NotBlank(message = "중복 체크 값은 필수입니다.")
    private String value;
}
