package com.sjiwon.anotherart.art.utils.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Art [Validator] -> ValidArtDuplicateResourceValidator 테스트")
class ValidArtDuplicateResourceValidatorTest {
    private ValidArtDuplicateResourceValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidArtDuplicateResourceValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("허용하지 않는 작품 중복체크 타입이 들어오면 validator를 통과하지 못한다")
    void notAllowedDuplicateResource() {
        // given
        final String unknown = "unknown";

        // when
        boolean actual = validator.isValid(unknown, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 작품 중복체크 타입이 들어오면 validator를 통과한다")
    void allowedDuplicateResource() {
        // given
        final String name = "name";

        // when
        boolean actual = validator.isValid(name, context);

        // then
        assertThat(actual).isTrue();
    }
}
