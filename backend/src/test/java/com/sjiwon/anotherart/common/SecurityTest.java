package com.sjiwon.anotherart.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.common.config.MySqlTestContainersExtension;
import com.sjiwon.anotherart.common.config.ObjectMapperConfiguration;
import com.sjiwon.anotherart.common.config.RedisTestContainersExtension;
import com.sjiwon.anotherart.common.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.SecurityConfiguration;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.token.domain.service.TokenManager;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(classes = {SecurityConfiguration.class, ObjectMapperConfiguration.class})
@ExtendWith({
        MySqlTestContainersExtension.class,
        RedisTestContainersExtension.class
})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class SecurityTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        createMember(MEMBER_A, 1L);
        doThrow(new UsernameNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()))
                .when(memberRepository)
                .findById(argThat(arg -> !arg.equals(1L)));
        doThrow(new UsernameNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()))
                .when(memberRepository)
                .findByLoginId(argThat(arg -> !arg.equals(MEMBER_A.getLoginId())));
    }

    private void createMember(final MemberFixture fixture, final Long memberId) {
        final Member member = fixture.toMember().apply(memberId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
    }

    protected String convertObjectToJson(final Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
}
