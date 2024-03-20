package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.global.ResponseWrapper;
import com.sjiwon.anotherart.global.annotation.Auth;
import com.sjiwon.anotherart.member.application.usecase.MemberPrivateQueryUseCase;
import com.sjiwon.anotherart.member.application.usecase.dto.PurchaseArtsAssembler;
import com.sjiwon.anotherart.member.application.usecase.dto.SoldArtsAssembler;
import com.sjiwon.anotherart.member.application.usecase.query.GetInformationById;
import com.sjiwon.anotherart.member.application.usecase.query.GetPointRecordsById;
import com.sjiwon.anotherart.member.application.usecase.query.GetPurchaseArtsById;
import com.sjiwon.anotherart.member.application.usecase.query.GetSoldArtsById;
import com.sjiwon.anotherart.member.application.usecase.query.GetWinningAuctionArtsById;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.WinningAuctionArt;
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
public class MemberInformationApiController {
    private final MemberPrivateQueryUseCase memberPrivateQueryUseCase;

    @Operation(summary = "기본 정보 조회 Endpoint")
    @GetMapping
    public ResponseEntity<MemberInformation> getInformation(@Auth final Authenticated authenticated) {
        final MemberInformation response = memberPrivateQueryUseCase.getInformation(new GetInformationById(authenticated.id()));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포인트 활용 내역 조회 Endpoint")
    @GetMapping("/points")
    public ResponseEntity<ResponseWrapper<List<MemberPointRecord>>> getPointRecords(@Auth final Authenticated authenticated) {
        final List<MemberPointRecord> response = memberPrivateQueryUseCase.getPointRecords(new GetPointRecordsById(authenticated.id()));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "낙찰된 경매 작품 조회 Endpoint")
    @GetMapping("/arts/winning-auction")
    public ResponseEntity<ResponseWrapper<List<WinningAuctionArt>>> getWinningAuctionArts(@Auth final Authenticated authenticated) {
        final List<WinningAuctionArt> response = memberPrivateQueryUseCase.getWinningAuctionArts(new GetWinningAuctionArtsById(authenticated.id()));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "판매한 작품 조회 Endpoint")
    @GetMapping("/arts/sold")
    public ResponseEntity<SoldArtsAssembler> getSoldArts(@Auth final Authenticated authenticated) {
        final SoldArtsAssembler response = memberPrivateQueryUseCase.getSoldArts(new GetSoldArtsById(authenticated.id()));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "구매한 작품 조회 Endpoint")
    @GetMapping("/arts/purchase")
    public ResponseEntity<PurchaseArtsAssembler> getPurchaseArts(@Auth final Authenticated authenticated) {
        final PurchaseArtsAssembler response = memberPrivateQueryUseCase.getPurchaseArts(new GetPurchaseArtsById(authenticated.id()));
        return ResponseEntity.ok(response);
    }
}
