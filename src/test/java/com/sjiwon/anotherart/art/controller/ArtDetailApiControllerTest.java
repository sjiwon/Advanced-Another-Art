package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ChangeArtDescriptionRequest;
import com.sjiwon.anotherart.art.controller.dto.request.UpdateArtHashtagRequest;
import com.sjiwon.anotherart.art.controller.utils.ChangeArtDescriptionRequestUtils;
import com.sjiwon.anotherart.art.controller.utils.UpdateArtHashtagRequestUtils;
import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.ROLE_USER;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtDetailApiController ?????????")
class ArtDetailApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("????????? ?????? ?????? ????????? [GET /api/art/duplicate]")
    class artNameDuplicateCheck {
        private static final String BASE_URL = "/api/art/duplicate";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
        void test1() throws Exception {
            // given
            final String artName = ArtFixture.A.name();

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("name", artName);

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
                                            parameterWithName("name").description("?????? ????????? ????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("???????????? ???????????? ?????? ????????? ????????????")
        void test2() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            final String artName = ArtFixture.A.name();
            doThrow(AnotherArtException.type(ArtErrorCode.INVALID_ART_NAME))
                    .when(artService)
                    .checkDuplicateArtName(artName);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("name", artName);

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
                                            parameterWithName("name").description("?????? ????????? ????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("???????????? ???????????? ?????? ?????? ????????? ?????? ????????? ????????????")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            final String artName = ArtFixture.A.name();
            doNothing()
                    .when(artService)
                    .checkDuplicateArtName(artName);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("name", artName);

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
                                            parameterWithName("name").description("?????? ????????? ????????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("?????? ?????? ?????? ????????? [PATCH /api/art/{artId}/description]")
    class changeDescription {
        private static final String BASE_URL = "/api/art/{artId}/description";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
        void test1() throws Exception {
            // given
            Art art = createMockArt(HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            final String changeDescription = "change-description";
            ChangeArtDescriptionRequest request = ChangeArtDescriptionRequestUtils.createRequest(changeDescription);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, artId)
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
                                            parameterWithName("artId").description("????????? ????????? ????????? ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("changeDescription").description("????????? ?????? ??????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("?????? ?????? ????????? ????????????")
        void test2() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            final String changeDescription = "change-description";
            ChangeArtDescriptionRequest request = ChangeArtDescriptionRequestUtils.createRequest(changeDescription);
            doNothing()
                    .when(artService)
                    .changeDescription(artId, changeDescription);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, artId)
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
                                            parameterWithName("artId").description("????????? ????????? ????????? ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("changeDescription").description("????????? ?????? ??????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("?????? ???????????? ?????? ????????? [PATCH /api/art/{artId}/hashtags]")
    class updateHashtags {
        private static final String BASE_URL = "/api/art/{artId}/hashtag";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
        void test1() throws Exception {
            // given
            Art art = createMockArt(HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(UPDATE_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, artId)
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
                                            parameterWithName("artId").description("??????????????? ????????? ????????? ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("hashtagList").description("??????????????? ???????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("???????????? ????????? ??????????????? ????????? ?????? ??????(1???)?????? ????????? ?????? ????????? ????????????")
        void test2() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(EMPTY_HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(EMPTY_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, artId)
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
                                            parameterWithName("artId").description("??????????????? ????????? ????????? ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("hashtagList").description("??????????????? ???????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("???????????? ????????? ??????????????? ????????? ?????? ??????(10???)?????? ????????? ?????? ????????? ????????????")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(OVERFLOW_HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(OVERFLOW_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, artId)
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
                                            parameterWithName("artId").description("??????????????? ????????? ????????? ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("hashtagList").description("??????????????? ???????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("????????? ???????????? ??????????????? ????????????")
        void test4() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(UPDATE_HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(UPDATE_HASHTAGS);
            doNothing()
                    .when(artService)
                    .updateArtHashtags(artId, UPDATE_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, artId)
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
                                            parameterWithName("artId").description("??????????????? ????????? ????????? ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("hashtagList").description("??????????????? ???????????? ?????????")
                                    )
                            )
                    );

            assertThat(art.getHashtagList()).containsAll(UPDATE_HASHTAGS);
        }
    }

    @Nested
    @DisplayName("?????? ?????? ????????? [DELETE /api/art/{artId}]")
    class deleteArt {
        private static final String BASE_URL = "/api/art/{artId}";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
        void test1() throws Exception {
            // given
            Long memberId = 1L;

            Art art = createMockArt(UPDATE_HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, artId);

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
                                            parameterWithName("artId").description("????????? ????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("?????? ???????????? ?????? ???????????? ?????? ?????? ????????? ????????? ?????? ????????? ????????????")
        void test2() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            doThrow(AnotherArtException.type(ArtErrorCode.INVALID_ART_DELETE_BY_ANONYMOUS))
                    .when(artService)
                    .deleteArt(artId, memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, artId)
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
                                            parameterWithName("artId").description("????????? ????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("?????? ????????? ????????? ????????? ?????? ????????? ????????? ????????? ????????????")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            doThrow(AnotherArtException.type(ArtErrorCode.ALREADY_SOLD_OUT))
                    .when(artService)
                    .deleteArt(artId, memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final ArtErrorCode expectedError = ArtErrorCode.ALREADY_SOLD_OUT;
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
                                    "ArtApi/DeleteGeneralArt/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("?????? ?????? ????????? ????????????")
        void test4() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            doNothing()
                    .when(artService)
                    .deleteArt(artId, memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, artId)
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
                                            parameterWithName("artId").description("????????? ????????? ID(PK)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("?????? ????????? ?????? ????????? ??????????????? ?????????????????? ????????? ??? ?????? ????????? ????????????")
        void test5() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            doThrow(AnotherArtException.type(ArtErrorCode.ALREADY_BID_EXISTS))
                    .when(artService)
                    .deleteArt(artId, memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final ArtErrorCode expectedError = ArtErrorCode.ALREADY_BID_EXISTS;
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
                                    "ArtApi/DeleteAuctionArt/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("?????? ?????? ????????? ????????????")
        void test6() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            Art art = createMockArt(HASHTAGS);
            Long artId = 1L;
            given(artFindService.findById(artId)).willReturn(art);

            doNothing()
                    .when(artService)
                    .deleteArt(artId, memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, artId)
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
                                            parameterWithName("artId").description("????????? ????????? ID(PK)")
                                    )
                            )
                    );
        }
    }

    private Art createMockArt(List<String> hashtags) {
        return ArtFixture.A.toArt(MemberFixture.A.toMember(), hashtags);
    }
}