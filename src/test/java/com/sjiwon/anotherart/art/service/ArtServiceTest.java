package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.hashtag.HashtagRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@DisplayName("Art [Service Layer] -> ArtService 테스트")
class ArtServiceTest extends ServiceTest {
    @InjectMocks
    ArtService artService;

    @Mock
    ArtFindService artFindService;

    @Mock
    HashtagRepository hashtagRepository;

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

    @Test
    @DisplayName("작품 설명을 수정한다")
    void test2() {
        // given
        final Member owner = MemberFixture.A.toMember();
        final Art art = ArtFixture.A.toArt(owner, HASHTAGS);
        final Long artId = 1L;
        given(artFindService.findById(artId)).willReturn(art);

        // when
        final String changeDescription = art.getDescription() + "change";
        artService.changeDescription(artId, changeDescription);

        // then
        assertThat(art.getDescription()).isEqualTo(changeDescription);
    }

    @Test
    @DisplayName("작품의 해시태그를 업데이트한다")
    void test3() {
        // given
        final Member owner = MemberFixture.A.toMember();
        final Art art = ArtFixture.A.toArt(owner, HASHTAGS);
        final Long artId = 1L;
        lenient().when(artFindService.findById(artId)).thenReturn(art);

        // when
        final List<String> updateHashtags = List.of("A", "B", "C", "F", "E", "T", "Y", "H");
        artService.updateHashtags(artId, updateHashtags);

        // then
        assertThat(art.getHashtagList()).containsAll(updateHashtags);
    }
}