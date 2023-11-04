package com.sjiwon.anotherart.art.utils.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("Art [Validator] -> ValidArtTypeValidator 테스트")
class ValidArtTypeValidatorTest {
    private ValidArtTypeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidArtTypeValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("허용하지 않는 작품 타입이 들어오면 validator를 통과하지 못한다")
    void notAllowedArtType() {
        // given
        final String unknown = "unknown";

        // when
        final boolean actual = validator.isValid(unknown, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 작품 타입이 들어오면 validator를 통과한다")
    void allowedArtType() {
        // given
        final String general = "general";
        final String auction = "auction";

        // when
        final boolean actual1 = validator.isValid(general, context);
        final boolean actual2 = validator.isValid(auction, context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
