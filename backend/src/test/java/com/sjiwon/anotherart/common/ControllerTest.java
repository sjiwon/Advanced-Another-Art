package com.sjiwon.anotherart.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.favorite.controller.FavoriteApiController;
import com.sjiwon.anotherart.favorite.service.FavoriteService;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.SecurityConfiguration;
import com.sjiwon.anotherart.member.controller.MemberApiController;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.member.service.MemberService;
import com.sjiwon.anotherart.token.controller.TokenReissueApiController;
import com.sjiwon.anotherart.token.service.TokenManager;
import com.sjiwon.anotherart.token.service.TokenReissueService;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Optional;

import static com.sjiwon.anotherart.fixture.MemberFixture.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest({
        // member
        MemberApiController.class,

        // Favorite
        FavoriteApiController.class,

        // Token
        TokenReissueApiController.class
})
@ExtendWith(RestDocumentationExtension.class)
@Import(SecurityConfiguration.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    // common & external
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    // common & internal
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private TokenManager tokenManager;

    // member
    @MockBean
    protected MemberService memberService;

    // favorite
    @MockBean
    protected FavoriteService favoriteService;

    // token
    @MockBean
    protected TokenReissueService tokenReissueService;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilters(
                        new CharacterEncodingFilter("UTF-8", true),
                        springSecurityFilterChain
                )
                .build();

        createMember(MEMBER_A, 1L);
        createMember(MEMBER_B, 2L);
        createMember(MEMBER_C, 3L);

        doThrow(new UsernameNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()))
                .when(memberRepository)
                .findById(argThat(arg -> !(arg.equals(1L) || arg.equals(2L) || arg.equals(3L))));
        doThrow(new UsernameNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()))
                .when(memberRepository)
                .findByLoginId(
                        argThat(arg ->
                                !arg.equals(MEMBER_A.getLoginId())
                                        && !arg.equals(MEMBER_B.getLoginId())
                                        && !arg.equals(MEMBER_C.getLoginId())
                        )
                );
    }

    private void createMember(MemberFixture fixture, Long memberId) {
        Member member = fixture.toMember();
        ReflectionTestUtils.setField(member, "id", memberId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
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
