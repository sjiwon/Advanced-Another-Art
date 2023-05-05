package com.sjiwon.anotherart.favorite.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.favorite.exception.FavoriteErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Favorite [Controller Layer] -> FavoriteApiController 테스트")
class FavoriteApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("찜 등록 API [POST /api/arts/{artId}/like] - AccessToken 필수")
    class like {
        private static final String BASE_URL = "/api/arts/{artId}/like";
        private static final Long ART_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("이미 찜 등록된 작품을 찜할 수 없다")
        void throwExceptionByAlreadyFavoriteMarked() throws Exception {
            // given
            doThrow(AnotherArtException.type(FavoriteErrorCode.ALREADY_FAVORITE_MARKED))
                    .when(favoriteService)
                    .like(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final FavoriteErrorCode expectedError = FavoriteErrorCode.ALREADY_FAVORITE_MARKED;
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
                                    "ArtApi/Favorite/Like/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("찜 등록 할 작품 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @WithMockUser
        @DisplayName("찜 등록에 성공한다")
        void success() throws Exception {
            // given
            given(favoriteService.like(any(), any())).willReturn(1L);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "ArtApi/Favorite/Like/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("찜 등록 할 작품 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("찜 취소 API [DELETE /api/arts/{artId}/like] - AccessToken 필수")
    class cancel {
        private static final String BASE_URL = "/api/arts/{artId}/like";
        private static final Long ART_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("찜 등록이 되지 않은 작품을 취소할 수 없다")
        void throwExceptionByNotFavoriteMarked() throws Exception {
            // given
            doThrow(AnotherArtException.type(FavoriteErrorCode.NOT_FAVORITE_MARKED))
                    .when(favoriteService)
                    .cancel(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final FavoriteErrorCode expectedError = FavoriteErrorCode.NOT_FAVORITE_MARKED;
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
                                    "ArtApi/Favorite/Cancel/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("찜 취소 할 작품 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @WithMockUser
        @DisplayName("찜 취소에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(favoriteService)
                    .cancel(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "ArtApi/Favorite/Cancel/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("찜 취소 할 작품 ID(PK)")
                                    )
                            )
                    );
        }
    }
}
