package com.sjiwon.anotherart.art.presentation;

import com.sjiwon.anotherart.art.application.usecase.RegisterArtUseCase;
import com.sjiwon.anotherart.art.application.usecase.ValidateArtResourceUseCase;
import com.sjiwon.anotherart.art.application.usecase.command.ValidateArtResourceCommand;
import com.sjiwon.anotherart.art.domain.model.ArtDuplicateResource;
import com.sjiwon.anotherart.art.presentation.request.RegisterArtRequest;
import com.sjiwon.anotherart.global.ResponseWrapper;
import com.sjiwon.anotherart.global.annotation.Auth;
import com.sjiwon.anotherart.token.domain.model.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "작품 리소스 중복체크 & 등록 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts")
public class RegisterArtApi {
    private final ValidateArtResourceUseCase validateArtResourceUseCase;
    private final RegisterArtUseCase registerArtUseCase;

    @Operation(summary = "작품 사용 가능 여부 확인 EndPoint (이름)")
    @GetMapping("/duplicate/{resource}")
    public ResponseEntity<ResponseWrapper<Boolean>> useable(
            @PathVariable final String resource,
            @RequestParam @NotBlank(message = "중복 체크 값은 필수입니다.") final String value
    ) {
        final boolean result = validateArtResourceUseCase.useable(new ValidateArtResourceCommand(
                ArtDuplicateResource.from(resource),
                value
        ));
        return ResponseEntity.ok(ResponseWrapper.from(result));
    }

    @Operation(summary = "작품 등록 Endpoint")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<Long>> registerArt(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final RegisterArtRequest request
    ) {
        final Long artId = registerArtUseCase.invoke(request.toCommand(authenticated.id()));
        return ResponseEntity.ok(ResponseWrapper.from(artId));
    }
}
