package com.sjiwon.anotherart.favorite.controller;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.favorite.exception.FavoriteErrorCode;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static com.sjiwon.anotherart.common.utils.MemberUtils.ROLE_USER;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Favorite [Controller Layer] -> FavoriteApiController ?????????")
class FavoriteApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("?????? ????????? ?????? ????????? [POST /api/art/{artId}/like]")
    class likeMarking {
        private static final String BASE_URL = "/api/art/{artId}/like";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
        void test1() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId);

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
                                    "FavoriteApi/LikeMarking/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ??? ?????? ID(PK)")
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
        @DisplayName("?????? ???????????? ????????? ????????? ????????? ????????? ??? ?????? ?????? ????????? ????????? ????????????")
        void test2() throws Exception {
            // given
            Long ownerId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(ownerId, ROLE_USER);

            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            doThrow(AnotherArtException.type(FavoriteErrorCode.INVALID_LIKE_REQUEST_BY_ART_OWNER))
                    .when(favoriteService)
                    .addLike(artId, ownerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final FavoriteErrorCode expectedError = FavoriteErrorCode.INVALID_LIKE_REQUEST_BY_ART_OWNER;
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
                                    "FavoriteApi/LikeMarking/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ??? ?????? ID(PK)")
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
        @DisplayName("?????? ????????? ????????? ??? ??? ??? ?????? ????????? ?????? ????????? ?????? ?????? ????????? ????????????")
        void test3() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long memberId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            doThrow(AnotherArtException.type(FavoriteErrorCode.ALREADY_LIKE_MARKING))
                    .when(favoriteService)
                    .addLike(artId, memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final FavoriteErrorCode expectedError = FavoriteErrorCode.ALREADY_LIKE_MARKING;
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
                                    "FavoriteApi/LikeMarking/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ??? ?????? ID(PK)")
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
        @DisplayName("????????? ?????? ????????? ????????? ????????????")
        void test4() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long memberId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            doNothing()
                    .when(favoriteService)
                    .addLike(artId, memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "FavoriteApi/LikeMarking/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ??? ?????? ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("?????? ????????? ?????? ????????? [DELETE /api/art/{artId}/like]")
    class likeCancel {
        private static final String BASE_URL = "/api/art/{artId}/like";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
        void test1() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
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
                                    "FavoriteApi/LikeMarkingCancel/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ????????? ?????? ID(PK)")
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
        @DisplayName("?????? ???????????? ????????? ????????? ????????? ????????? ?????? ????????? ??? ??? ?????? ?????? ????????? ????????? ????????????")
        void test2() throws Exception {
            // given
            Long ownerId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(ownerId, ROLE_USER);

            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            doThrow(AnotherArtException.type(FavoriteErrorCode.INVALID_LIKE_REQUEST_BY_ART_OWNER))
                    .when(favoriteService)
                    .removeLike(artId, ownerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final FavoriteErrorCode expectedError = FavoriteErrorCode.INVALID_LIKE_REQUEST_BY_ART_OWNER;
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
                                    "FavoriteApi/LikeMarkingCancel/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ????????? ?????? ID(PK)")
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
        @DisplayName("????????? ????????? ??? ?????? ????????? ?????? ????????? ????????? ????????? ????????? ?????? ????????? ?????? ?????? ????????? ????????????")
        void test3() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long memberId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            doThrow(AnotherArtException.type(FavoriteErrorCode.NEVER_OR_ALREADY_CANCEL))
                    .when(favoriteService)
                    .removeLike(artId, memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final FavoriteErrorCode expectedError = FavoriteErrorCode.NEVER_OR_ALREADY_CANCEL;
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
                                    "FavoriteApi/LikeMarkingCancel/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ????????? ?????? ID(PK)")
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
        @DisplayName("????????? ?????? ????????? ????????? ????????????")
        void test4() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long memberId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            doNothing()
                    .when(favoriteService)
                    .removeLike(artId, memberId);

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
                                    "FavoriteApi/LikeMarkingCancel/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("????????? ????????? ????????? ?????? ID(PK)")
                                    )
                            )
                    );
        }
    }

    private Art createMockArt(List<String> hashtags) {
        return ArtFixture.A.toArt(MemberFixture.A.toMember(), hashtags);
    }
}