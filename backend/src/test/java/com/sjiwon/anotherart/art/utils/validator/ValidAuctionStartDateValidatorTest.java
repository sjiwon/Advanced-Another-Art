package com.sjiwon.anotherart.art.utils.validator;

import com.sjiwon.anotherart.common.UnitTest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Art -> ValidAuctionStartDateValidator 테스트")
class ValidAuctionStartDateValidatorTest extends UnitTest {
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final ValidAuctionStartDateValidator sut = new ValidAuctionStartDateValidator();

    @Test
    @DisplayName("일반 작품[auctionStartDate == null]일 경우 validator를 검증없이 통과한다")
    void allowAllGeneralArt() {
        // when
        final boolean actual = sut.isValid(null, context);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("경매 작품이고 auctionStartDate가 현재 시간 이전이면 validator를 통과하지 못한다")
    void notAllowedWhenAuctionStartDateIsBeforeFromNowOn() {
        // given
        final LocalDateTime auctionStartDate = LocalDateTime.now().minusHours(1);

        // when
        final boolean actual = sut.isValid(auctionStartDate, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("경매 작품이고 auctionStartDate가 현재 시간 이후면 validator를 통과한다")
    void allowedWhenAuctionStartDateIsAfterFromNowOn() {
        // given
        final LocalDateTime auctionStartDate = LocalDateTime.now().plusHours(1);

        // when
        final boolean actual = sut.isValid(auctionStartDate, context);

        // then
        assertThat(actual).isTrue();
    }
}
