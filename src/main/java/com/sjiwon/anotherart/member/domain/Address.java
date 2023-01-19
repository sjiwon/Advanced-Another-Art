package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.member.exception.MemberException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {
    private static final int POSTCODE_LENGTH = 5;

    @Column(name = "postcode", nullable = false)
    private int postcode;

    @Column(name = "default_address", nullable = false)
    private String defaultAddress;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    private Address(int postcode, String defaultAddress, String detailAddress) {
        this.postcode = postcode;
        this.defaultAddress = defaultAddress;
        this.detailAddress = detailAddress;
    }

    public static Address of(int postcode, String defaultAddress, String detailAddress) {
        validatePostcodeLength(postcode);
        validateAddressIsNotBlank(defaultAddress, detailAddress);
        return new Address(postcode, defaultAddress, detailAddress);
    }

    public Address update(int postcode, String defaultAddress, String detailAddress) {
        validatePostcodeLength(postcode);
        validateAddressIsNotBlank(defaultAddress, detailAddress);
        return new Address(postcode, defaultAddress, detailAddress);
    }

    private static void validatePostcodeLength(int postcode) {
        if (isInvalidPostcodeLength(postcode)) {
            throw MemberException.type(MemberErrorCode.INVALID_POST_CODE);
        }
    }

    private static boolean isInvalidPostcodeLength(int postcode) {
        return String.valueOf(postcode).length() != POSTCODE_LENGTH;
    }

    private static void validateAddressIsNotBlank(String defaultAddress, String detailAddress) {
        if (isAddressEmpty(defaultAddress, detailAddress)) {
            throw MemberException.type(MemberErrorCode.INVALID_ADDRESS);
        }
    }

    private static boolean isAddressEmpty(String defaultAddress, String detailAddress) {
        return !StringUtils.hasText(defaultAddress) || !StringUtils.hasText(detailAddress);
    }
}
