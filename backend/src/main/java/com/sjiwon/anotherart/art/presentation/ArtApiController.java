package com.sjiwon.anotherart.art.presentation;

import com.sjiwon.anotherart.art.application.usecase.RegisterArtUseCase;
import com.sjiwon.anotherart.art.application.usecase.ValidateArtResourceUseCase;
import com.sjiwon.anotherart.art.application.usecase.command.RegisterArtCommand;
import com.sjiwon.anotherart.art.application.usecase.command.ValidateArtResourceCommand;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.art.presentation.dto.request.ArtDuplicateCheckRequest;
import com.sjiwon.anotherart.art.presentation.dto.request.RegisterArtRequest;
import com.sjiwon.anotherart.art.presentation.dto.response.ArtIdResponse;
import com.sjiwon.anotherart.file.utils.converter.FileConverter;
import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final RegisterArtUseCase registerArtUseCase;

    @Operation(summary = "작품명 중복체크 EndPoint")
    @PostMapping("/duplicate/name")
    public ResponseEntity<Void> checkName(@RequestBody @Valid final ArtDuplicateCheckRequest request) {
        validateArtResourceUseCase.invoke(new ValidateArtResourceCommand(NAME, request.value()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "작품 등록 Endpoint")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtIdResponse> registerArt(
            @ExtractPayload final Long memberId,
            @ModelAttribute @Valid final RegisterArtRequest request
    ) {
        final Long savedArtId = registerArtUseCase.invoke(new RegisterArtCommand(
                memberId,
                ArtName.from(request.name()),
                Description.from(request.description()),
                ArtType.from(request.type()),
                request.price(),
                request.auctionStartDate(),
                request.auctionEndDate(),
                request.hashtags(),
                FileConverter.convertImageFile(request.file())
        ));

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/arts/{id}").build(savedArtId))
                .body(new ArtIdResponse(savedArtId));
    }
}
