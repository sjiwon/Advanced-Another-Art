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

@DisplayName("Favorite [Controller Layer] -> FavoriteApiController 테스트")
class FavoriteApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("작품 좋아요 등록 테스트 [POST /api/art/{artId}/like]")
    class likeMarking {
        private static final String BASE_URL = "/api/art/{artId}/like";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
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
                                            parameterWithName("artId").description("좋아요 등록을 할 작품 ID(PK)")
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
        @DisplayName("작품 소유자는 본인의 작품을 좋아요 등록할 수 없고 그에 따라서 예외가 발생한다")
        void test2() throws Exception {
            // given
            Long ownerId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(ownerId);

            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            doThrow(AnotherArtException.type(FavoriteErrorCode.INVALID_LIKE_REQUEST_BY_ART_OWNER))
                    .when(favoriteService)
                    .like(artId, ownerId);

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
                                            parameterWithName("artId").description("좋아요 등록을 할 작품 ID(PK)")
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
        @DisplayName("이미 좋아요 등록을 한 후 또 다시 좋아요 등록 요청을 하게 되면 예외가 발생한다")
        void test3() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long memberId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId);

            doThrow(AnotherArtException.type(FavoriteErrorCode.ALREADY_LIKE_MARKING))
                    .when(favoriteService)
                    .like(artId, memberId);

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
                                            parameterWithName("artId").description("좋아요 등록을 할 작품 ID(PK)")
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
        @DisplayName("작품에 대한 좋아요 등록을 성공한다")
        void test4() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long memberId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId);

            doNothing()
                    .when(favoriteService)
                    .like(artId, memberId);

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
                                            parameterWithName("artId").description("좋아요 등록을 할 작품 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("작품 좋아요 취소 테스트 [DELETE /api/art/{artId}/like]")
    class likeCancel {
        private static final String BASE_URL = "/api/art/{artId}/like";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
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
                                            parameterWithName("artId").description("좋아요 취소를 진행할 작품 ID(PK)")
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
        @DisplayName("작품 소유자는 본인의 작품에 대해서 좋아요 취소 요청을 할 수 없고 그에 따라서 예외가 발생한다")
        void test2() throws Exception {
            // given
            Long ownerId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(ownerId);

            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            doThrow(AnotherArtException.type(FavoriteErrorCode.INVALID_LIKE_REQUEST_BY_ART_OWNER))
                    .when(favoriteService)
                    .likeCancel(artId, ownerId);

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
                                            parameterWithName("artId").description("좋아요 취소를 진행할 작품 ID(PK)")
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
        @DisplayName("좋아요 등록을 한 적이 없거나 이미 취소한 작품에 대해서 좋아요 취소 요청을 하게 되면 예외가 발생한다")
        void test3() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long memberId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId);

            doThrow(AnotherArtException.type(FavoriteErrorCode.NEVER_OR_ALREADY_CANCEL))
                    .when(favoriteService)
                    .likeCancel(artId, memberId);

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
                                            parameterWithName("artId").description("좋아요 취소를 진행할 작품 ID(PK)")
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
        @DisplayName("작품에 대한 좋아요 취소를 성공한다")
        void test4() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long memberId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId);

            doNothing()
                    .when(favoriteService)
                    .likeCancel(artId, memberId);

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
                                            parameterWithName("artId").description("좋아요 취소를 진행할 작품 ID(PK)")
                                    )
                            )
                    );
        }
    }

    private Art createMockArt(List<String> hashtags) {
        return ArtFixture.A.toArt(MemberFixture.A.toMember(), hashtags);
    }
}