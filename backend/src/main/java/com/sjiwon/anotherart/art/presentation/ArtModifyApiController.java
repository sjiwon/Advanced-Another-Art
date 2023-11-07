package com.sjiwon.anotherart.art.presentation;

import com.sjiwon.anotherart.art.application.usecase.DeleteArtUseCase;
import com.sjiwon.anotherart.art.application.usecase.UpdateArtUseCase;
import com.sjiwon.anotherart.art.application.usecase.command.DeleteArtCommand;
import com.sjiwon.anotherart.art.application.usecase.command.UpdateArtCommand;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.art.presentation.dto.request.UpdateArtRequest;
import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class ArtModifyApiController {
    private final UpdateArtUseCase updateArtUseCase;
    private final DeleteArtUseCase deleteArtUseCase;

    @Operation(summary = "작품 수정 EndPoint")
    @PreAuthorize("@artOwnerValidator.isArtOwner(#artId, #memberId)")
    @PatchMapping
    public ResponseEntity<Void> update(
            @ExtractPayload final Long memberId,
            @PathVariable final Long artId,
            @RequestBody @Valid final UpdateArtRequest request
    ) {
        updateArtUseCase.invoke(new UpdateArtCommand(
                artId,
                ArtName.from(request.name()),
                Description.from(request.description()),
                request.hashtags()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "작품 삭제 EndPoint")
    @PreAuthorize("@artOwnerValidator.isArtOwner(#artId, #memberId)")
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @ExtractPayload final Long memberId,
            @PathVariable final Long artId
    ) {
        deleteArtUseCase.invoke(new DeleteArtCommand(artId));
        return ResponseEntity.noContent().build();
    }
}
