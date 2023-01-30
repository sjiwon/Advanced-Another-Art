package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberDetailApiController 테스트")
@RequiredArgsConstructor
class MemberDetailApiControllerTest extends ControllerTest {
    private final MockMvc mockMvc;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String BEARER_TOKEN = "Bearer ";

    @Nested
    @DisplayName("사용자 닉네임 수정 테스트 [PATCH /api/member/nickname]")
    class changeNickname {
        private static final String BASE_URL = "/api/member/nickname";
        
        @Test
        @DisplayName("이전에 사용하던 닉네임에 대해서 수정 요청을 보내면 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member member = createMemberA();
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            final String changeNickname = member.getNickname();

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("changeNickname", changeNickname)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED);

            // then
            final MemberErrorCode expectedError = MemberErrorCode.NICKNAME_SAME_AS_BEFORE;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "MemberApi/ChangeNickname/failure/case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("changeNickname").description("변경할 닉네임")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }
        
        @Test
        @DisplayName("타인이 사용하는 닉네임으로 수정 요청을 보내면 예외가 발생한다")
        void test2() throws Exception {
            // given
            Member memberA = createMemberA();
            Member memberB = createMemberB();
            String accessToken = jwtTokenProvider.createAccessToken(memberA.getId());
            final String changeNickname = memberB.getNickname();

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("changeNickname", changeNickname)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED);

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "MemberApi/ChangeNickname/failure/case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("changeNickname").description("변경할 닉네임")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("닉네임 수정에 성공한다")
        void test3() throws Exception {
            // given
            Member member = createMemberA();
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            final String changeNickname = member.getNickname() + "hello world";

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("changeNickname", changeNickname)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/ChangeNickname/success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("changeNickname").description("변경할 닉네임")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 로그인 아이디를 찾는 테스트 [GET /api/member/find/id]")
    class findLoginId {
        private static final String BASE_URL = "/api/member/find/id";

        @Test
        @DisplayName("Access Token으로 사용자 아이디를 조회한다")
        void test() throws Exception {
            // given
            Member member = createMemberA();
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.value").value(member.getLoginId()))
                    .andDo(
                            document(
                                    "MemberApi/FindId/success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("value").description("사용자 로그인 아이디")
                                    )
                            )
                    );
        }
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember(PasswordEncoderUtils.getEncoder()));
    }


    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember(PasswordEncoderUtils.getEncoder()));
    }
}