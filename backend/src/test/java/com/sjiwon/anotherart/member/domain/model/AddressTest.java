package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> 도메인 [Address] 테스트")
class AddressTest extends UnitTest {
    private static final String DEFAULT_ADDRESS = "경기 성남시 분당구 정자일로 95";
    private static final String DETAIL_ADDRESS = "네이버";

    @Nested
    @DisplayName("Address 생성")
    class Construct {
        @ParameterizedTest(name = "{index}: 우편번호={0}, 주소={1}, 상세주소={2}")
        @MethodSource("postcodeDoNotMatch")
        @DisplayName("주소의 우편번호(postcode)는 반드시 5자리 숫자여야 합니다")
        void throwExceptionByInvalidPostCode(final int postcode, final String defaultAddress, final String detailAddress) {
            assertThatThrownBy(() -> Address.of(postcode, defaultAddress, detailAddress))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.INVALID_POST_CODE.getMessage());
        }

        private static Stream<Arguments> postcodeDoNotMatch() {
            return Stream.of(
                    Arguments.of(1, DEFAULT_ADDRESS, DETAIL_ADDRESS),
                    Arguments.of(12, DEFAULT_ADDRESS, DETAIL_ADDRESS),
                    Arguments.of(123, DEFAULT_ADDRESS, DETAIL_ADDRESS),
                    Arguments.of(1234, DEFAULT_ADDRESS, DETAIL_ADDRESS)
            );
        }

        @ParameterizedTest(name = "{index}: 우편번호={0}, 주소={1}, 상세주소={2}")
        @MethodSource("defaultOrDetailAddressDoNotMatch")
        @DisplayName("주소 또는 상세주소는 반드시 null이나 빈 값이 아니여야 합니다")
        void throwExceptionByInvalidAddress(final int postcode, final String defaultAddress, final String detailAddress) {
            assertThatThrownBy(() -> Address.of(postcode, defaultAddress, detailAddress))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.INVALID_ADDRESS.getMessage());
        }

        private static Stream<Arguments> defaultOrDetailAddressDoNotMatch() {
            return Stream.of(
                    Arguments.of(12345, null, null),
                    Arguments.of(12345, DEFAULT_ADDRESS, null),
                    Arguments.of(12345, null, DETAIL_ADDRESS),
                    Arguments.of(12345, "", ""),
                    Arguments.of(12345, DEFAULT_ADDRESS, ""),
                    Arguments.of(12345, "", DETAIL_ADDRESS)
            );
        }

        @Test
        @DisplayName("Address를 생성한다")
        void construct() {
            final Address address = Address.of(12345, DEFAULT_ADDRESS, DETAIL_ADDRESS);

            assertAll(
                    () -> assertThat(address.getPostcode()).isEqualTo(12345),
                    () -> assertThat(address.getDefaultAddress()).isEqualTo(DEFAULT_ADDRESS),
                    () -> assertThat(address.getDetailAddress()).isEqualTo(DETAIL_ADDRESS)
            );
        }
    }
}
