package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;

import static com.sjiwon.anotherart.art.controller.utils.ArtRegisterRequestUtils.createAuctionArtRegisterRequest;
import static com.sjiwon.anotherart.art.controller.utils.ArtRegisterRequestUtils.createGeneralArtRegisterRequest;
import static com.sjiwon.anotherart.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.ArtFixture.GENERAL_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtApiController 테스트")
class ArtApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("작품 등록 API [POST /api/art] - AccessToken 필수")
    class registerArt {
        private static final String BASE_URL = "/api/art";

        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        private MultipartFile file;

        @BeforeEach
        void setUp() throws IOException {
            file = createSingleMockMultipartFile("1.png", "image/png");
        }

        @Test
        @DisplayName("이미 사용하고 있는 작품명이면 등록에 실패한다")
        void throwExceptionByDuplicateName() throws Exception {
            // given
            doThrow(AnotherArtException.type(ArtErrorCode.DUPLICATE_NAME))
                    .when(artService)
                    .registerArt(any(), any());

            // when
            final ArtRegisterRequest request = createGeneralArtRegisterRequest(GENERAL_1, file, Set.of("A", "B", "C", "D", "E"));
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("name", request.name())
                    .param("description", request.description())
                    .param("type", request.type())
                    .param("price", String.valueOf(request.price()))
                    .params(createHashtagParams(request.hashtags()));

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
                                    "ArtApi/Register/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    queryParameters(
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("type").description("작품 타입")
                                                    .attributes(constraint("general / auction")),
                                            parameterWithName("price").description("작품 가격"),
                                            parameterWithName("auctionStartDate").description("경매 시작날짜")
                                                    .optional()
                                                    .attributes(constraint("type=auction일 경우 필수")),
                                            parameterWithName("auctionEndDate").description("경매 종료날짜")
                                                    .optional()
                                                    .attributes(constraint("type=auction일 경우 필수")),
                                            parameterWithName("hashtags").description("해시태그")
                                                    .attributes(constraint("1개 ~ 10개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("일반 작품을 등록한다")
        void successGeneral() throws Exception {
            // given
            given(artService.registerArt(any(), any())).willReturn(1L);

            // when
            final ArtRegisterRequest request = createGeneralArtRegisterRequest(GENERAL_1, file, Set.of("A", "B", "C", "D", "E"));
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("name", request.name())
                    .param("description", request.description())
                    .param("type", request.type())
                    .param("price", String.valueOf(request.price()))
                    .params(createHashtagParams(request.hashtags()));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "ArtApi/Register/Success/General",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    queryParameters(
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("type").description("작품 타입")
                                                    .attributes(constraint("general / auction")),
                                            parameterWithName("price").description("작품 가격"),
                                            parameterWithName("auctionStartDate").description("경매 시작날짜")
                                                    .optional()
                                                    .attributes(constraint("type=auction일 경우 필수")),
                                            parameterWithName("auctionEndDate").description("경매 종료날짜")
                                                    .optional()
                                                    .attributes(constraint("type=auction일 경우 필수")),
                                            parameterWithName("hashtags").description("해시태그")
                                                    .attributes(constraint("1개 ~ 10개"))
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("경매 작품을 등록한다")
        void successAuction() throws Exception {
            // given
            given(artService.registerArt(any(), any())).willReturn(1L);

            // when
            final ArtRegisterRequest request = createAuctionArtRegisterRequest(AUCTION_1, file, Set.of("A", "B", "C", "D", "E"));
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("name", request.name())
                    .param("description", request.description())
                    .param("type", request.type())
                    .param("price", String.valueOf(request.price()))
                    .param("auctionStartDate", request.auctionStartDate().format(DATE_TIME_FORMATTER))
                    .param("auctionEndDate", request.auctionEndDate().format(DATE_TIME_FORMATTER))
                    .params(createHashtagParams(request.hashtags()));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "ArtApi/Register/Success/Auction",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    queryParameters(
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("type").description("작품 타입")
                                                    .attributes(constraint("general / auction")),
                                            parameterWithName("price").description("작품 가격"),
                                            parameterWithName("auctionStartDate").description("경매 시작날짜")
                                                    .optional()
                                                    .attributes(constraint("type=auction일 경우 필수")),
                                            parameterWithName("auctionEndDate").description("경매 종료날짜")
                                                    .optional()
                                                    .attributes(constraint("type=auction일 경우 필수")),
                                            parameterWithName("hashtags").description("해시태그")
                                                    .attributes(constraint("1개 ~ 10개"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("중복 체크 API [GET /api/art/check-duplicates] - AccessToken 필수")
    class checkDuplicates {
        private static final String BASE_URL = "/api/art/check-duplicates";

        @Test
        @DisplayName("해당 값이 중복됨에 따라 예외가 발생한다")
        void throwExceptionByDuplicateResource() throws Exception {
            // given
            doThrow(AnotherArtException.type(ArtErrorCode.DUPLICATE_NAME))
                    .when(artService)
                    .duplicateCheck(any(), any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", "name")
                    .param("value", "중복체크작품명");

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
                                    "ArtApi/Register/DuplicateCheck/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    queryParameters(
                                            parameterWithName("resource").description("중복 체크 타입")
                                                    .attributes(constraint("name")),
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
                    .when(artService)
                    .duplicateCheck(any(), any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", "name")
                    .param("value", "중복체크작품명");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "ArtApi/Register/DuplicateCheck/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    queryParameters(
                                            parameterWithName("resource").description("중복 체크 타입")
                                                    .attributes(constraint("name")),
                                            parameterWithName("value").description("중복 체크 값")
                                    )
                            )
                    );
        }
    }

    private MultiValueMap<String, String> createHashtagParams(final Set<String> hashtags) {
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.addAll("hashtags", new ArrayList<>(hashtags));
        return map;
    }
}
