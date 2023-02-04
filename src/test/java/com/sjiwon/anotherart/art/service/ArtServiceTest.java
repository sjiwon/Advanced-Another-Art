package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.service.MemberFindService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

@DisplayName("Art [Service Layer] -> ArtService 테스트")
class ArtServiceTest extends ServiceTest {
    @InjectMocks
    ArtService artService;

    @Mock
    ArtRepository artRepository;

    @Mock
    MemberFindService memberFindService;

    @Mock
    AuctionRepository auctionRepository;

    @Test
    @DisplayName("작품명 중복 체크 로직을 진행한다")
    void test1() {
        // given
        final String duplicateName = "duplicate-name";
        final String uniqueName = "unique-name";
        given(artRepository.existsByName(duplicateName)).willReturn(true);
        given(artRepository.existsByName(uniqueName)).willReturn(false);

        // when - then
        assertThatThrownBy(() -> artService.artNameDuplicateCheck(duplicateName))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.INVALID_ART_NAME.getMessage());
        assertDoesNotThrow(() -> artService.artNameDuplicateCheck(uniqueName));
    }
}