package com.sjiwon.anotherart.global.annotation.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("Art [Validator] -> ValidDuplicateResourceValidator 테스트")
class ValidDuplicateResourceValidatorTest {
    private ValidDuplicateResourceValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidDuplicateResourceValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("허용하지 않는 중복체크 타입이 들어오면 validator를 통과하지 못한다")
    void notAllowedDuplicateResource() {
        // given
        final String unknown = "unknown";

        // when
        boolean actual = validator.isValid(unknown, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 중복체크 타입이 들어오면 validator를 통과한다")
    void allowedDuplicateResource() {
        // given
        final String nickname = "nickname";
        final String loginId = "loginId";
        final String phone = "phone";
        final String email = "email";

        // when
        boolean actual1 = validator.isValid(nickname, context);
        boolean actual2 = validator.isValid(loginId, context);
        boolean actual3 = validator.isValid(phone, context);
        boolean actual4 = validator.isValid(email, context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isTrue()
        );
    }
}
