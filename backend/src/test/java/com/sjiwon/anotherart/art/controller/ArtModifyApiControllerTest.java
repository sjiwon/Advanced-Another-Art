package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtModifyRequest;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Set;

import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtModifyApiController 테스트")
class ArtModifyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("작품 정보 수정 API [PATCH /api/arts/{artId}] - AccessToken 필수")
    class changeNickname {
        private static final String BASE_URL = "/api/arts/{artId}";
        private static final Long ART_ID = 1L;
        
        @Test
        @WithMockUser
        @DisplayName("다른 작품이 사용하고 있는 작품명으로 수정할 수 없다")
        void throwExceptionByDuplicateName() throws Exception {
            // given
            doThrow(AnotherArtException.type(ArtErrorCode.DUPLICATE_NAME))
                    .when(artService)
                    .update(any(), any(), any(), any());

            // when
            final ArtModifyRequest request = new ArtModifyRequest("수정할 작품명", "수정할 작품 설명", Set.of("hello", "world"));
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final ArtErrorCode expectedError = ArtErrorCode.DUPLICATE_NAME;
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
                                    "ArtApi/Update/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("작품 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("수정할 작품명"),
                                            fieldWithPath("description").description("수정할 작품 설명"),
                                            fieldWithPath("hashtags[]").description("수정할 해시태그")
                                                    .attributes(constraint("1개 ~ 10개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @WithMockUser
        @DisplayName("작품 정보를 수정한다")
        void sucess() throws Exception {
            // given
            doNothing()
                    .when(artService)
                    .update(any(), any(), any(), any());

            // when
            final ArtModifyRequest request = new ArtModifyRequest("수정할 작품명", "수정할 작품 설명", Set.of("hello", "world"));
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "ArtApi/Update/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("작품 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("수정할 작품명"),
                                            fieldWithPath("description").description("수정할 작품 설명"),
                                            fieldWithPath("hashtags[]").description("수정할 해시태그")
                                                    .attributes(constraint("1개 ~ 10개"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("작품 삭제 API [DELETE /api/arts/{artId}] - AccessToken 필수")
    class delete {
        private static final String BASE_URL = "/api/arts/{artId}";
        private static final Long ART_ID = 1L;

        @Test
        @DisplayName("작품이 판매되었다면 삭제할 수 없다")
        void throwExceptionByCannotDeleteSoldArt() throws Exception {
            // given
            doThrow(AnotherArtException.type(ArtErrorCode.CANNOT_DELETE_SOLD_ART))
                    .when(artService)
                    .delete(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final ArtErrorCode expectedError = ArtErrorCode.CANNOT_DELETE_SOLD_ART;
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
                                    "ArtApi/Delete/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("작품 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("입찰 기록이 존재하는 경매 작품은 삭제할 수 없다")
        void throwExceptionByCannotDeleteIfBidExists() throws Exception {
            // given
            doThrow(AnotherArtException.type(ArtErrorCode.CANNOT_DELETE_IF_BID_EXISTS))
                    .when(artService)
                    .delete(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final ArtErrorCode expectedError = ArtErrorCode.CANNOT_DELETE_IF_BID_EXISTS;
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
                                    "ArtApi/Delete/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("작품 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("삭제에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(artService)
                    .delete(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, ART_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "ArtApi/Delete/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("artId").description("작품 ID(PK)")
                                    )
                            )
                    );
        }
    }
}
