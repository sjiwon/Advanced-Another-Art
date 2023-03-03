package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChangeArtDescriptionRequest {
    @NotBlank(message = ArtRequestValidationMessage.ChangeArtInfo.CHANGE_DESCRIPTION)
    private String changeDescription;
}
