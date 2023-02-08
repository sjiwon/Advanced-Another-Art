package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ChangeArtDescriptionRequest;
import com.sjiwon.anotherart.art.controller.dto.request.UpdateArtHashtagRequest;
import com.sjiwon.anotherart.art.controller.utils.ChangeArtDescriptionRequestUtils;
import com.sjiwon.anotherart.art.controller.utils.UpdateArtHashtagRequestUtils;
import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtDetailApiController 테스트")
class ArtDetailApiControllerTest extends ControllerTest {
    private static final ArtFixture AUCTION_ART = ArtFixture.A;
    private static final ArtFixture GENERAL_ART = ArtFixture.B;

    @Nested
    @DisplayName("작품명 중복 체크 테스트 [POST /api/art/duplicate-check]")
    class artNameDuplicateCheck {
        private static final String BASE_URL = "/api/art/duplicate-check";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .param("artName", art.getName());

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtApi/DuplicateCheck/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("artName").description("중복 체크를 진행할 작품명")
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
        @DisplayName("작품명이 중복됨에 따라 예외가 발생한다")
        void test2() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artName", art.getName());

            // then
            final ArtErrorCode expectedError = ArtErrorCode.INVALID_ART_NAME;
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
                                    "ArtApi/DuplicateCheck/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("artName").description("중복 체크를 진행할 작품명")
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
        @DisplayName("작품명이 중복되지 않기 때문에 중복 체크에 성공한다")
        void test3() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artName", art.getName() + "diff");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "ArtApi/DuplicateCheck/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("artName").description("중복 체크를 진행할 작품명")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("작품 설명 수정 테스트 [PATCH /api/art/{artId}/description]")
    class changeDescription {
        private static final String BASE_URL = "/api/art/{artId}/description";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);
            final String changeDescription = art.getDescription() + "change";
            ChangeArtDescriptionRequest request = ChangeArtDescriptionRequestUtils.createRequest(changeDescription);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtApi/ChangeDescription/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("설명을 수정할 작품의 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("changeDescription").description("변경할 작품 설명")
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
        @DisplayName("작품 설명 수정에 성공한다")
        void test2() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);
            final String changeDescription = art.getDescription() + "change";
            ChangeArtDescriptionRequest request = ChangeArtDescriptionRequestUtils.createRequest(changeDescription);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "ArtApi/ChangeDescription/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("설명을 수정할 작품의 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("changeDescription").description("변경할 작품 설명")
                                    )
                            )
                    );

            assertThat(art.getDescription()).isEqualTo(changeDescription);
        }
    }

    @Nested
    @DisplayName("작품 해시태그 수정 테스트 [PATCH /api/art/{artId}/hashtags]")
    class updateHashtags {
        private static final String BASE_URL = "/api/art/{artId}/hashtags";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);
            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(UPDATE_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtApi/UpdateHashtags/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("해시태그를 수정할 작품의 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("hashtagList").description("업데이트할 해시태그 리스트")
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
        @DisplayName("업데이트 하려는 해시태그의 개수가 최소 개수(1개)보다 적음에 따라 예외가 발생한다")
        void test2() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);
            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(EMPTY_HASHTAGS);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = String.format(ArtRequestValidationMessage.ART_HASHTAG_LIST_MIN, 1);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(message))
                    .andDo(
                            document(
                                    "ArtApi/UpdateHashtags/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("해시태그를 수정할 작품의 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("hashtagList").description("업데이트할 해시태그 리스트")
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
        @DisplayName("업데이트 하려는 해시태그의 개수가 최대 개수(10개)보다 많음에 따라 예외가 발생한다")
        void test3() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);
            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(OVERFLOW_HASHTAGS);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = String.format(ArtRequestValidationMessage.ART_HASHTAG_LIST_MAX, 10);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(message))
                    .andDo(
                            document(
                                    "ArtApi/UpdateHashtags/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("해시태그를 수정할 작품의 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("hashtagList").description("업데이트할 해시태그 리스트")
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
        @DisplayName("작품의 해시태그 업데이트에 성공한다")
        void test4() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);
            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(UPDATE_HASHTAGS);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "ArtApi/UpdateHashtags/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("해시태그를 수정할 작품의 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("hashtagList").description("업데이트할 해시태그 리스트")
                                    )
                            )
                    );

            assertThat(art.getHashtagList()).containsAll(UPDATE_HASHTAGS);
        }
    }

    @Nested
    @DisplayName("작품 삭제 테스트 [DELETE /api/art/{artId}]")
    class deleteArt {
        private static final String BASE_URL = "/api/art/{artId}";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, art.getId());

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtApi/DeleteGeneralArt/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("삭제할 작품의 ID(PK)")
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
        @DisplayName("작품 소유자가 아닌 사용자가 작품 삭제 요청을 보냄에 따라 예외가 발생한다")
        void test2() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);

            // when
            Member memberB = createMemberB();
            final String accessToken = jwtTokenProvider.createAccessToken(memberB.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, art.getId())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final ArtErrorCode expectedError = ArtErrorCode.INVALID_ART_DELETE_BY_ANONYMOUS;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtApi/DeleteGeneralArt/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("삭제할 작품의 ID(PK)")
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
        @DisplayName("이미 판매된 작품에 대해서 삭제 요청을 보내면 예외가 발생한다")
        void test3() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createSoldOutGeneralArt(owner);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, art.getId())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final ArtErrorCode expectedError = ArtErrorCode.ALREADY_SALE;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtApi/DeleteGeneralArt/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("삭제할 작품의 ID(PK)")
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
        @DisplayName("일반 작품 삭제에 성공한다")
        void test4() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, art.getId())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "ArtApi/DeleteGeneralArt/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("삭제할 작품의 ID(PK)")
                                    )
                            )
                    );

            assertThat(artRepository.findById(art.getId())).isEmpty();
        }

        @Test
        @DisplayName("경매 작품일 경우 입찰이 한번이라도 진행되었다면 삭제할 수 없고 예외가 발생한다")
        void test5() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createBidProcessAuctionArt(owner);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, art.getId())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final ArtErrorCode expectedError = ArtErrorCode.ALREADY_BID_EXISTS;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtApi/DeleteAuctionArt/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("삭제할 작품의 ID(PK)")
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
        @DisplayName("경매 작품 삭제에 성공한다")
        void test6() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, art.getId())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "ArtApi/DeleteAuctionArt/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("삭제할 작품의 ID(PK)")
                                    )
                            )
                    );

            assertThat(artRepository.findById(art.getId())).isEmpty();
        }
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember());
    }

    private Art createGeneralArt(Member owner) {
        Art art = GENERAL_ART.toArt(owner, HASHTAGS);
        return artRepository.save(art);
    }

    private Art createSoldOutGeneralArt(Member owner) {
        Art art = GENERAL_ART.toArt(owner, HASHTAGS);
        art.changeArtStatus(ArtStatus.SOLD_OUT);
        return artRepository.save(art);
    }

    private Art createAuctionArt(Member owner) {
        Art art = artRepository.save(AUCTION_ART.toArt(owner, HASHTAGS));
        auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
        return art;
    }
    
    private Art createBidProcessAuctionArt(Member owner) {
        Art art = artRepository.save(AUCTION_ART.toArt(owner, HASHTAGS));

        // 입찰 진행
        Auction auction = auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
        Member memberB = createMemberB();
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, memberB, AUCTION_ART.toArt(owner, HASHTAGS).getPrice() + 1_000_000));
        return art;
    }
}