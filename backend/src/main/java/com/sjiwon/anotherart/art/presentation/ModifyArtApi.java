package com.sjiwon.anotherart.art.presentation;

import com.sjiwon.anotherart.art.application.usecase.DeleteArtUseCase;
import com.sjiwon.anotherart.art.application.usecase.UpdateArtUseCase;
import com.sjiwon.anotherart.art.application.usecase.command.DeleteArtCommand;
import com.sjiwon.anotherart.art.presentation.request.UpdateArtRequest;
import com.sjiwon.anotherart.global.annotation.Auth;
import com.sjiwon.anotherart.token.domain.model.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "작품 수정/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts/{artId}")
public class ModifyArtApi {
    private final UpdateArtUseCase updateArtUseCase;
    private final DeleteArtUseCase deleteArtUseCase;

    @Operation(summary = "작품 수정 EndPoint")
    @PatchMapping
    public ResponseEntity<Void> update(
            @Auth final Authenticated authenticated,
            @PathVariable final Long artId,
            @RequestBody @Valid final UpdateArtRequest request
    ) {
        updateArtUseCase.invoke(request.toCommand(authenticated.id(), artId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "작품 삭제 EndPoint")
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Auth final Authenticated authenticated,
            @PathVariable final Long artId
    ) {
        deleteArtUseCase.invoke(new DeleteArtCommand(authenticated.id(), artId));
        return ResponseEntity.noContent().build();
    }
}
