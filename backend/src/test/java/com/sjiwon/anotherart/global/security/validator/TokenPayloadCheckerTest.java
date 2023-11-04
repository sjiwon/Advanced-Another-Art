package com.sjiwon.anotherart.global.security.validator;

import com.sjiwon.anotherart.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Security [Validator] -> TokenPayloadChecker 테스트")
class TokenPayloadCheckerTest extends ServiceTest {
    @Autowired
    private TokenPayloadChecker tokenPayloadChecker;

    @Test
    @DisplayName("Token PayloadId와 PathVariable memberId가 동일한지 확인한다")
    void isTrustworthyMember() {
        // when
        final boolean actual1 = tokenPayloadChecker.isTrustworthyMember(1L, 1L);
        final boolean actual2 = tokenPayloadChecker.isTrustworthyMember(1L, 2L);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
