package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.art.controller.utils.ArtRegistrationRequestUtils;
import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.ROLE_USER;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtApiController 테스트")
class ArtApiControllerTest extends ControllerTest {
    private static final ArtFixture AUCTION_ART = ArtFixture.A;
    private static final ArtFixture GENERAL_ART = ArtFixture.B;
    private static final ArtFixture GENERAL_ART_BMP = ArtFixture.B_BMP;

    @Nested
    @DisplayName("작품 등록 테스트 [POST /api/art]")
    class registrationArt {
        private static final String BASE_URL = "/api/art";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            ArtRegisterRequest request = ArtRegistrationRequestUtils.createGeneralArtRequest(GENERAL_ART, HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()));

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
                                    "ArtApi/RegistrationArt/General/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)")
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
        @DisplayName("Content-Type이 multipart/form-data가 아님에 따라 예외가 발생한다")
        void test2() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createGeneralArtRequest(GENERAL_ART, HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .param("file", Arrays.toString(request.getFile().getBytes())) // Content-Type 예외를 위해서 단순하게 param에 끼워넣기
                    .params(createHashtagParams(request.getHashtagList()));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.MEDIA_TYPE_ERROR;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnsupportedMediaType())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtApi/RegistrationArt/General/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("file").description("작품 이미지"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)")
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
        @DisplayName("작품 사진이 업로드되지 않음에 따라 예외가 발생한다")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createGeneralArtRequestWithEmptyImage(GENERAL_ART, HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = ArtRequestValidationMessage.Registration.ART_FILE_EMPTY;
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
                                    "ArtApi/RegistrationArt/General/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)")
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
        @DisplayName("지원하지 않는 이미지 포맷으로 인해 예외가 발생한다")
        void test4() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createGeneralArtRequestWithNotAcceptableFormat(GENERAL_ART_BMP, HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = ArtRequestValidationMessage.Registration.ART_FILE_FORMAT;
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
                                    "ArtApi/RegistrationArt/General/Failure/Case4",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)")
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
        @DisplayName("작품의 해시태그가 없음에 따라 예외가 발생한다")
        void test5() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createGeneralArtRequest(GENERAL_ART, EMPTY_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()));

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
                                    "ArtApi/RegistrationArt/General/Failure/Case5",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)")
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
        @DisplayName("작품의 해시태그가 최대 허용 개수(10개)보다 많음에 따라 예외가 발생한다")
        void test6() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createGeneralArtRequest(GENERAL_ART, OVERFLOW_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()));

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
                                    "ArtApi/RegistrationArt/General/Failure/Case6",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)")
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
        @DisplayName("일반 작품 등록에 성공한다")
        void test7() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createGeneralArtRequest(GENERAL_ART, HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "ArtApi/RegistrationArt/General/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("경매 시작 날짜가 현재 시각 이전으로 설정됨에 따라 예외가 발생한다")
        void test8() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createAuctionArtRequest(AUCTION_ART, HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()))
                    .param("startDate", currentTime3DayAgoToString)
                    .param("endDate", currentTime3DayLaterToString);

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = ArtRequestValidationMessage.Registration.AUCTION_ART_START_DATE;
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
                                    "ArtApi/RegistrationArt/Auction/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)"),
                                            parameterWithName("startDate").optional().description("경매 시작 날짜 (경매 작품은 필수)"),
                                            parameterWithName("endDate").optional().description("경매 종료 날짜 (경매 작품은 필수)")
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
        @DisplayName("경매 시작 날짜가 종료 날짜 이후로 설정됨에 따라 예외가 발생한다")
        void test9() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createAuctionArtRequest(AUCTION_ART, HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()))
                    .param("startDate", currentTime3DayLaterToString)
                    .param("endDate", currentTime1DayLaterToString);

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.INVALID_AUCTION_DURATION;
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
                                    "ArtApi/RegistrationArt/Auction/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)"),
                                            parameterWithName("startDate").optional().description("경매 시작 날짜 (경매 작품은 필수)"),
                                            parameterWithName("endDate").optional().description("경매 종료 날짜 (경매 작품은 필수)")
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
        @DisplayName("경매 작품 등록에 성공한다")
        void test10() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            ArtRegisterRequest request = ArtRegistrationRequestUtils.createAuctionArtRequest(AUCTION_ART, HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) request.getFile())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("artType", request.getArtType())
                    .param("name", request.getName())
                    .param("description", request.getDescription())
                    .param("price", String.valueOf(request.getPrice()))
                    .params(createHashtagParams(request.getHashtagList()))
                    .param("startDate", currentTime1DayLaterToString)
                    .param("endDate", currentTime3DayLaterToString);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "ArtApi/RegistrationArt/Auction/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("file").description("작품 이미지")
                                    ),
                                    requestParameters(
                                            parameterWithName("artType").description("작품 타입 (경매 / 일반)"),
                                            parameterWithName("name").description("작품명"),
                                            parameterWithName("description").description("작품 설명"),
                                            parameterWithName("price").description("작품 가격 (경매 작품일 경우 초기 입찰 가격)"),
                                            parameterWithName("hashtagList").description("작품 해시태그 (1개 ~ 10개)"),
                                            parameterWithName("startDate").optional().description("경매 시작 날짜 (경매 작품은 필수)"),
                                            parameterWithName("endDate").optional().description("경매 종료 날짜 (경매 작품은 필수)")
                                    )
                            )
                    );
        }
    }

    private MultiValueMap<String, String> createHashtagParams(List<String> hashtags) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.addAll("hashtagList", hashtags);
        return map;
    }
}
