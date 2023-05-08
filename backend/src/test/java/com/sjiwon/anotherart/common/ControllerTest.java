package com.sjiwon.anotherart.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.art.controller.ArtApiController;
import com.sjiwon.anotherart.art.controller.ArtModifyApiController;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.auction.controller.BidApiController;
import com.sjiwon.anotherart.auction.service.BidService;
import com.sjiwon.anotherart.favorite.controller.FavoriteApiController;
import com.sjiwon.anotherart.favorite.service.FavoriteService;
import com.sjiwon.anotherart.member.controller.MemberApiController;
import com.sjiwon.anotherart.member.controller.MemberInformationApiController;
import com.sjiwon.anotherart.member.controller.MemberModifyApiController;
import com.sjiwon.anotherart.member.controller.MemberPointApiController;
import com.sjiwon.anotherart.member.service.MemberInformationService;
import com.sjiwon.anotherart.member.service.MemberPointService;
import com.sjiwon.anotherart.member.service.MemberService;
import com.sjiwon.anotherart.purchase.controller.PurchaseApiController;
import com.sjiwon.anotherart.purchase.service.PurchaseService;
import com.sjiwon.anotherart.token.controller.TokenReissueApiController;
import com.sjiwon.anotherart.token.service.TokenReissueService;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest({
        // art
        ArtApiController.class, ArtModifyApiController.class,

        // auction
        BidApiController.class,

        // Favorite
        FavoriteApiController.class,

        // member
        MemberApiController.class, MemberModifyApiController.class, MemberPointApiController.class,
        MemberInformationApiController.class,

        // purchase
        PurchaseApiController.class,

        // Token
        TokenReissueApiController.class
})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    // common
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    // art
    @MockBean
    protected ArtService artService;

    // auction
    @MockBean
    protected BidService bidService;

    // favorite
    @MockBean
    protected FavoriteService favoriteService;

    // member
    @MockBean
    protected MemberService memberService;

    @MockBean
    protected MemberPointService memberPointService;

    @MockBean
    protected MemberInformationService memberInformationService;

    // purchase
    @MockBean
    protected PurchaseService purchaseService;

    // token
    @MockBean
    protected TokenReissueService tokenReissueService;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(prettyPrint());
    }

    protected OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    protected Snippet getHeaderWithAccessToken() {
        return requestHeaders(
                headerWithName(AUTHORIZATION).description("Access Token")
        );
    }

    protected Snippet getHeaderWithRefreshToken() {
        return requestHeaders(
                headerWithName(AUTHORIZATION).description("Refresh Token")
        );
    }

    protected Snippet getExceptionResponseFiels() {
        return responseFields(
                fieldWithPath("status").description("HTTP 상태 코드"),
                fieldWithPath("errorCode").description("커스텀 예외 코드"),
                fieldWithPath("message").description("예외 메시지")
        );
    }

    protected Attributes.Attribute constraint(String value) {
        return new Attributes.Attribute("constraints", value);
    }

    protected String convertObjectToJson(Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
}
