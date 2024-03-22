package com.sjiwon.anotherart.global.lock;

import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import com.sjiwon.anotherart.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Global/Lock -> DistributedLockNameGenerator 테스트")
class DistributedLockNameGeneratorTest extends UnitTest {
    @Test
    @DisplayName("주어진 Key를 파싱한다 - 표현식")
    void caseA() {
        // given
        final String prefix = "AUCTION:";
        final String key = "2 == 2";
        final String[] parameterNames = {};
        final Object[] args = {};

        // when
        final Object result = DistributedLockNameGenerator.generate(prefix, key, parameterNames, args);

        // then
        assertThat(result).isEqualTo("AUCTION:true");
    }

    @Test
    @DisplayName("주어진 Key를 파싱한다 - 단일 값")
    void caseB() {
        // given
        final String prefix = "AUCTION:";
        final String key = "#value";
        final String[] parameterNames = {"value"};
        final Object[] args = {3};

        // when
        final Object result = DistributedLockNameGenerator.generate(prefix, key, parameterNames, args);

        // then
        assertThat(result).isEqualTo("AUCTION:3");
    }

    @Test
    @DisplayName("주어진 Key를 파싱한다 - 다중 값")
    void caseC() {
        // given
        final String prefix = "AUCTION:";
        final String key = "#value1 + #value2";
        final String[] parameterNames = {"value1", "value2"};
        final Object[] args = {"Test", 5};

        // when
        final Object result = DistributedLockNameGenerator.generate(prefix, key, parameterNames, args);

        // then
        assertThat(result).isEqualTo("AUCTION:Test5");
    }

    @Test
    @DisplayName("주어진 Key를 파싱한다 - 객체 값")
    void caseD() {
        // given
        final String prefix = "AUCTION:";
        final String key = "#command.auctionId";
        final String[] parameterNames = {"command"};
        final Object[] args = {new BidCommand(1L, 1L, 1500)};

        // when
        final Object result = DistributedLockNameGenerator.generate(prefix, key, parameterNames, args);

        // then
        assertThat(result).isEqualTo("AUCTION:1");
    }
}
