package com.sjiwon.anotherart.acceptance.security;

import com.sjiwon.anotherart.common.AcceptanceTest;
import com.sjiwon.anotherart.common.config.DatabaseCleanerEachCallbackExtension;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.sjiwon.anotherart.acceptance.security.SecurityAcceptanceProcessor.시큐리티_로그인을_진행한다;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] Security 인증 관련 기능")
public class SecurityAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("Security 로그인 API")
    class SecurityLoginApi {
        // TODO Member 회원가입 API 리팩토링 후 수정
//        @Test
        @DisplayName("아이디에 해당하는 사용자가 존재하지 않으면 로그인에 실패한다")
        void failureByAnonymousId() {
            시큐리티_로그인을_진행한다(null, null)
                    .statusCode(MemberErrorCode.MEMBER_NOT_FOUND.getStatus().value())
                    .body("errorCode", is(MemberErrorCode.MEMBER_NOT_FOUND.getErrorCode()))
                    .body("message", is(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()));
        }

        //        @Test
        @DisplayName("패스워드가 일치하지 않으면 로그인에 실패한다")
        void failureByAnonymousPassword() {
            시큐리티_로그인을_진행한다(null, null)
                    .statusCode(MemberErrorCode.INVALID_PASSWORD.getStatus().value())
                    .body("errorCode", is(MemberErrorCode.INVALID_PASSWORD.getErrorCode()))
                    .body("message", is(MemberErrorCode.INVALID_PASSWORD.getMessage()));
        }

        //        @Test
        @DisplayName("로그인에 성공한다")
        void success() {
            시큐리티_로그인을_진행한다(null, null)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("nickname", is(MEMBER_A.getNickname()))
                    .body("accessToken", notNullValue(String.class))
                    .body("refreshToken", notNullValue(String.class));
        }
    }
}
