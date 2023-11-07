package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Art -> 도메인 [ArtName VO] 테스트")
class ArtNameTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("ArtName이 null이거나 공백이면 생성에 실패한다")
    void throwExceptionByNameIsBlank(final String value) {
        assertThatThrownBy(() -> ArtName.from(value))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.NAME_IS_BLANK.getMessage());
    }

    @Test
    @DisplayName("ArtName이 길이 제한을 넘어선다면 생성에 실패한다")
    void throwExceptionByNameLengthOutOfRange() {
        final String value = "a".repeat(21);

        assertThatThrownBy(() -> ArtName.from(value))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.NAME_LENGTH_OUT_OF_RANGE.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "aaaaaaaaaaaaaaaaaaaa"})
    @DisplayName("ArtName을 생성한다")
    void construct(final String value) {
        final ArtName name = ArtName.from(value);
        assertThat(name.getValue()).isEqualTo(value);
    }
}
