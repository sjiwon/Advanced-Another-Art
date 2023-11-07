package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.ArtType.GENERAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> 도메인 [ArtType VO] 테스트")
public class ArtTypeTest {
    @Test
    @DisplayName("ArtType을 조회한다")
    void findSpecificArtType() {
        assertAll(
                () -> assertThat(ArtType.from("general")).isEqualTo(GENERAL),
                () -> assertThat(ArtType.from("auction")).isEqualTo(AUCTION)
        );
    }

    @Test
    @DisplayName("유효하지 않은 값으로 ArtType을 조회할 수 없다")
    void throwExceptionByArtTypeIsWeird() {
        assertThatThrownBy(() -> ArtType.from("anonymous"))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.INVALID_ART_TYPE.getMessage());
    }
}
