package com.sjiwon.anotherart.acceptance.token;

import com.sjiwon.anotherart.common.config.DatabaseCleanerEachCallbackExtension;
import com.sjiwon.anotherart.token.exception.TokenErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.sjiwon.anotherart.acceptance.token.TokenAcceptanceProcessor.토큰을_재발급받는다;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 토큰 재발급 관련 기능")
public class TokenAcceptanceTest {
    @Nested
    @DisplayName("토큰 재발급 API")
    class TokenReissueApi {
        // TODO Member 회원가입 API 리팩토링 후 수정

        @Test
        @DisplayName("사용자 소유의 RefreshToken이 아니면 재발급받을 수 없다")
        void failure() {
            final String refreshToken = null;
//                    = MEMBER_A.회원가입_후_로그인을_진행한다().refreshToken();
            토큰을_재발급받는다(refreshToken)
                    .statusCode(TokenErrorCode.INVALID_TOKEN.getStatus().value())
                    .body("errorCode", is(TokenErrorCode.INVALID_TOKEN.getErrorCode()))
                    .body("message", is(TokenErrorCode.INVALID_TOKEN.getMessage()));
        }

        @Test
        @DisplayName("RefreshToken을 통해서 AccessToken + RefreshToken을 재발급받는다")
        void success() {
            final String refreshToken = null;
//                    = MEMBER_A.회원가입_후_로그인을_진행한다().refreshToken();
            토큰을_재발급받는다(refreshToken)
                    .statusCode(OK.value())
                    .body("accessToken", notNullValue(String.class))
                    .body("refreshToken", notNullValue(String.class));
        }
    }
}
