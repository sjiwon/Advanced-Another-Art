package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ChangeArtDescriptionRequest;
import com.sjiwon.anotherart.art.controller.dto.request.UpdateArtHashtagRequest;
import com.sjiwon.anotherart.art.controller.utils.ChangeArtDescriptionRequestUtils;
import com.sjiwon.anotherart.art.controller.utils.UpdateArtHashtagRequestUtils;
import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtDetailApiController 테스트")
@RequiredArgsConstructor
class ArtDetailApiControllerTest extends ControllerTest {
    private final MockMvc mockMvc;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ArtRepository artRepository;
    private final AuctionRepository auctionRepository;

    private static final String BEARER_TOKEN = "Bearer ";

    private static final ArtFixture AUCTION_ART = ArtFixture.A;
    private static final ArtFixture GENERAL_ART = ArtFixture.B;
    private static final List<String> EMPTY_HASHTAGS = List.of();
    private static final List<String> HASHTAGS = List.of("A", "B", "C", "D", "E");
    private static final List<String> UPDATE_HASHTAGS = List.of("F", "G", "H", "I", "J");
    private static final List<String> OVERFLOW_HASHTAGS = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K");

    private static final LocalDateTime currentTime1DayLater = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime currentTime3DayLater = LocalDateTime.now().plusDays(3);

    @Nested
    @DisplayName("작품명 중복 체크 테스트 [POST /api/art/duplicate-check]")
    class artNameDuplicateCheck {
        private static final String BASE_URL = "/api/art/duplicate-check";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member owner = createMember();
            Art art = createGeneralArt(owner);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED_VALUE)
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
            Member owner = createMember();
            String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            Art art = createGeneralArt(owner);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED_VALUE)
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
        @DisplayName("작품명이 중복되지 않음에 따라 중복 체크에 성공한다")
        void test3() throws Exception {
            // given
            Member owner = createMember();
            String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            Art art = createGeneralArt(owner);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED_VALUE)
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
            Member owner = createMember();
            Art art = createGeneralArt(owner);
            ChangeArtDescriptionRequest request = ChangeArtDescriptionRequestUtils.createRequest(art.getDescription() + "change");

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON_VALUE)
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
            Member owner = createMember();
            String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            Art art = createGeneralArt(owner);
            final String changeDescription = art.getDescription() + "change";
            ChangeArtDescriptionRequest request = ChangeArtDescriptionRequestUtils.createRequest(changeDescription);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
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
            Member owner = createMember();
            Art art = createGeneralArt(owner);
            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(UPDATE_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON_VALUE)
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
            Member owner = createMember();
            String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            Art art = createGeneralArt(owner);
            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(EMPTY_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON_VALUE)
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
            Member owner = createMember();
            String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            Art art = createGeneralArt(owner);
            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(OVERFLOW_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON_VALUE)
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
            Member owner = createMember();
            String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            Art art = createGeneralArt(owner);
            UpdateArtHashtagRequest request = UpdateArtHashtagRequestUtils.createRequest(UPDATE_HASHTAGS);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, art.getId())
                    .contentType(APPLICATION_JSON_VALUE)
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

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Art createGeneralArt(Member owner) {
        Art art = GENERAL_ART.toArt(owner);
        art.applyHashtags(new HashSet<>(HASHTAGS));
        return artRepository.save(art);
    }

    private Art createAuctionArt(Member owner) {
        Art art = AUCTION_ART.toArt(owner);
        art.applyHashtags(new HashSet<>(HASHTAGS));
        Art savedArt = artRepository.save(art);
        auctionRepository.save(Auction.initAuction(savedArt, Period.of(currentTime1DayLater, currentTime3DayLater)));
        return savedArt;
    }
}