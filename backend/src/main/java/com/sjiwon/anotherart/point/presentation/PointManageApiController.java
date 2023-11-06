package com.sjiwon.anotherart.point.presentation;


import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.point.application.usecase.ManagePointUseCase;
import com.sjiwon.anotherart.point.application.usecase.command.ChargePointCommand;
import com.sjiwon.anotherart.point.application.usecase.command.RefundPointCommand;
import com.sjiwon.anotherart.point.presentation.dto.request.ChargePointRequest;
import com.sjiwon.anotherart.point.presentation.dto.request.RefundPointRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "포인트 충전/환불 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointManageApiController {
    private final ManagePointUseCase managePointUseCase;

    @Operation(summary = "포인트 충전 Endpoint")
    @PostMapping("/charge")
    public ResponseEntity<Void> chargePoint(
            @ExtractPayload final Long memberId,
            @RequestBody @Valid final ChargePointRequest request
    ) {
        managePointUseCase.charge(new ChargePointCommand(memberId, request.chargeAmount()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "포인트 환불 Endpoint")
    @PostMapping("/refund")
    public ResponseEntity<Void> refundPoint(
            @ExtractPayload final Long memberId,
            @RequestBody @Valid final RefundPointRequest request
    ) {
        managePointUseCase.refund(new RefundPointCommand(memberId, request.refundAmount()));
        return ResponseEntity.noContent().build();
    }
}
