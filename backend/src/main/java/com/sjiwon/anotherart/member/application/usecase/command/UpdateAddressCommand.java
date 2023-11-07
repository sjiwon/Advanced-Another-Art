package com.sjiwon.anotherart.member.application.usecase.command;

public record UpdateAddressCommand(
        Long memberId,
        int postcode,
        String defaultAddress,
        String detailAddress
) {
}
