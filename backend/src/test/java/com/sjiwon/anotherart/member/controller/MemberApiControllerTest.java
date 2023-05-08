package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberApiController 테스트")
class MemberApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("회원가입 API [POST /api/member]")
    class signUp {
        private static final String BASE_URL = "/api/member";

        @Test
        @DisplayName("중복되는 값(닉네임)에 의해서 회원가입에 실패한다")
        void throwExceptionByDuplicateNickname() throws Exception {
            // given
            doThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME))
                    .when(memberService)
                    .signUp(any());

            // when
            final SignUpRequest request = createSignUpRequest();
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("password").description("로그인 비밀번호"),
                                            fieldWithPath("school").description("재학중인 학교"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("postcode").description("우편번호"),
                                            fieldWithPath("defaultAddress").description("기본 주소"),
                                            fieldWithPath("detailAddress").description("상세 주소")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("회원가입에 성공한다")
        void success() throws Exception {
            // given
            given(memberService.signUp(any())).willReturn(1L);

            // when
            final SignUpRequest request = createSignUpRequest();
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("password").description("로그인 비밀번호"),
                                            fieldWithPath("school").description("재학중인 학교"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("postcode").description("우편번호"),
                                            fieldWithPath("defaultAddress").description("기본 주소"),
                                            fieldWithPath("detailAddress").description("상세 주소")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("중복 체크 API [GET /api/member/check-duplicates]")
    class checkDuplicates {
        private static final String BASE_URL = "/api/member/check-duplicates";

        @Test
        @DisplayName("중복 체크 대상이 아니면 예외가 발생한다")
        void throwExceptionByNotAllowedDuplicateResource() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", "anonymous")
                    .param("value", "중복체크닉네임");

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "중복 체크 대상이 아닙니다.";
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(message)
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/DuplicateCheck/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입")
                                                    .attributes(constraint("nickname / loginId / phone / email")),
                                            parameterWithName("value").description("중복 체크 값")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("해당 값이 중복됨에 따라 예외가 발생한다")
        void throwExceptionByDuplicateResource() throws Exception {
            // given
            doThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME))
                    .when(memberService)
                    .duplicateCheck(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", "nickname")
                    .param("value", "중복체크닉네임");

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/DuplicateCheck/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입")
                                                    .attributes(constraint("nickname / loginId / phone / email")),
                                            parameterWithName("value").description("중복 체크 값")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("해당 값이 중복되지 않음에 따라 중복 체크를 통과한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(memberService)
                    .duplicateCheck(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", "nickname")
                    .param("value", "중복체크닉네임");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/SignUp/DuplicateCheck/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입")
                                                    .attributes(constraint("nickname / loginId / phone / email")),
                                            parameterWithName("value").description("중복 체크 값")
                                    )
                            )
                    );
        }
    }

    private SignUpRequest createSignUpRequest() {
        return new SignUpRequest(
                MEMBER_A.getName(),
                MEMBER_A.getNickname(),
                MEMBER_A.getLoginId(),
                MEMBER_A.getPassword(),
                "경기대학교",
                "01012345678",
                MEMBER_A.getEmail(),
                MEMBER_A.getPostcode(),
                MEMBER_A.getDefaultAddress(),
                MEMBER_A.getDetailAddress()
        );
    }
}
