package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.global.ResponseWrapper;
import com.sjiwon.anotherart.global.annotation.Auth;
import com.sjiwon.anotherart.member.application.usecase.MemberInfoQueryUseCase;
import com.sjiwon.anotherart.member.application.usecase.query.response.MemberInformationResponse;
import com.sjiwon.anotherart.member.application.usecase.query.response.PointRecordResponse;
import com.sjiwon.anotherart.member.application.usecase.query.response.PurchaseArtAssembler;
import com.sjiwon.anotherart.member.application.usecase.query.response.SoldArtAssembler;
import com.sjiwon.anotherart.member.application.usecase.query.response.WinningAuctionArtResponse;
import com.sjiwon.anotherart.token.domain.model.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "사용자 활동 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/me")
public class QueryMemberInfoApi {
    private final MemberInfoQueryUseCase memberInfoQueryUseCase;

    @Operation(summary = "기본 정보 조회 Endpoint")
    @GetMapping
    public ResponseEntity<MemberInformationResponse> getInformation(
            @Auth final Authenticated authenticated
    ) {
        final MemberInformationResponse response = memberInfoQueryUseCase.getInformation(authenticated.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포인트 활용 내역 조회 Endpoint")
    @GetMapping("/points")
    public ResponseEntity<ResponseWrapper<List<PointRecordResponse>>> getPointRecords(
            @Auth final Authenticated authenticated
    ) {
        final List<PointRecordResponse> response = memberInfoQueryUseCase.getPointRecords(authenticated.id());
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "낙찰된 경매 작품 조회 Endpoint")
    @GetMapping("/arts/winning-auction")
    public ResponseEntity<ResponseWrapper<List<WinningAuctionArtResponse>>> getWinningAuctionArts(
            @Auth final Authenticated authenticated
    ) {
        final List<WinningAuctionArtResponse> response = memberInfoQueryUseCase.getWinningAuctionArts(authenticated.id());
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "판매한 작품 조회 Endpoint")
    @GetMapping("/arts/sold")
    public ResponseEntity<SoldArtAssembler> getSoldArts(
            @Auth final Authenticated authenticated
    ) {
        final SoldArtAssembler response = memberInfoQueryUseCase.getSoldArts(authenticated.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "구매한 작품 조회 Endpoint")
    @GetMapping("/arts/purchase")
    public ResponseEntity<PurchaseArtAssembler> getPurchaseArts(
            @Auth final Authenticated authenticated
    ) {
        final PurchaseArtAssembler response = memberInfoQueryUseCase.getPurchaseArts(authenticated.id());
        return ResponseEntity.ok(response);
    }
}
