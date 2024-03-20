package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.INVALID_ADDRESS;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.INVALID_POST_CODE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Address {
    private static final int POSTCODE_LENGTH = 5;

    @Column(name = "postcode", nullable = false)
    private int postcode;

    @Column(name = "default_address", nullable = false)
    private String defaultAddress;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    private Address(final int postcode, final String defaultAddress, final String detailAddress) {
        this.postcode = postcode;
        this.defaultAddress = defaultAddress;
        this.detailAddress = detailAddress;
    }

    public static Address of(final int postcode, final String defaultAddress, final String detailAddress) {
        validatePostcodeLength(postcode);
        validateAddressIsNotBlank(defaultAddress, detailAddress);
        return new Address(postcode, defaultAddress, detailAddress);
    }

    public Address update(final int postcode, final String defaultAddress, final String detailAddress) {
        validatePostcodeLength(postcode);
        validateAddressIsNotBlank(defaultAddress, detailAddress);
        return new Address(postcode, defaultAddress, detailAddress);
    }

    private static void validatePostcodeLength(final int postcode) {
        if (isInvalidPostcodeLength(postcode)) {
            throw new MemberException(INVALID_POST_CODE);
        }
    }

    private static boolean isInvalidPostcodeLength(final int postcode) {
        return String.valueOf(postcode).length() != POSTCODE_LENGTH;
    }

    private static void validateAddressIsNotBlank(final String defaultAddress, final String detailAddress) {
        if (isAddressEmpty(defaultAddress, detailAddress)) {
            throw new MemberException(INVALID_ADDRESS);
        }
    }

    private static boolean isAddressEmpty(final String defaultAddress, final String detailAddress) {
        return isEmpty(defaultAddress) || isEmpty(detailAddress);
    }

    private static boolean isEmpty(final String str) {
        return !StringUtils.hasText(str);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final Address other = (Address) object;

        if (postcode != other.postcode) return false;
        if (!defaultAddress.equals(other.defaultAddress)) return false;
        return detailAddress.equals(other.detailAddress);
    }

    @Override
    public int hashCode() {
        int result = postcode;
        result = 31 * result + defaultAddress.hashCode();
        result = 31 * result + detailAddress.hashCode();
        return result;
    }
}
