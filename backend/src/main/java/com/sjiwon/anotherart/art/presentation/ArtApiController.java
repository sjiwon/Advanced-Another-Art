package com.sjiwon.anotherart.art.presentation;

import com.sjiwon.anotherart.art.application.ArtService;
import com.sjiwon.anotherart.art.application.usecase.ValidateArtResourceUseCase;
import com.sjiwon.anotherart.art.application.usecase.command.ValidateArtResourceCommand;
import com.sjiwon.anotherart.art.presentation.dto.request.ArtDuplicateCheckRequest;
import com.sjiwon.anotherart.art.presentation.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static com.sjiwon.anotherart.art.domain.model.ArtDuplicateResource.NAME;

@Tag(name = "작품 리소스 중복 체크 & 등록 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts")
public class ArtApiController {
    private final ValidateArtResourceUseCase validateArtResourceUseCase;
    private final ArtService artService;

    @Operation(summary = "작품명 중복체크 EndPoint")
    @PostMapping("/duplicate/name")
    public ResponseEntity<Void> checkName(@RequestBody @Valid final ArtDuplicateCheckRequest request) {
        validateArtResourceUseCase.invoke(new ValidateArtResourceCommand(NAME, request.value()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerArt(@ExtractPayload final Long ownerId,
                                            @ModelAttribute @Valid final ArtRegisterRequest request) {
        final Long savedArtId = artService.registerArt(ownerId, request);

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/arts/{id}").build(savedArtId))
                .build();
    }
}
