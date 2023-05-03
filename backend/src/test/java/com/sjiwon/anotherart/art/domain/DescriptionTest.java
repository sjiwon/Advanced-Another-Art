package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Art 도메인 {Description VO} 테스트")
class DescriptionTest {
    @Test
    @DisplayName("Description이 공백이면 생성에 실패한다")
    void throwExceptionByDescriptionIsBlank() {
        assertThatThrownBy(() -> Description.from(""))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.DESCRIPTION_IS_BLANK.getMessage());
    }

    @Test
    @DisplayName("Description이 1000자가 넘어갈 경우 생성에 실패한다")
    void throwExceptionByDescriptionLengthOutOfRange() {
        final String value = "a".repeat(1001);

        assertThatThrownBy(() -> Description.from(value))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.DESCRIPTION_LENGTH_OUT_OF_RANGE.getMessage());
    }

    @Test
    @DisplayName("Description을 생성한다")
    void construct() {
        final String value = "a".repeat(999);

        Description description = Description.from(value);
        assertThat(description.getValue()).isEqualTo(value);
    }
}
