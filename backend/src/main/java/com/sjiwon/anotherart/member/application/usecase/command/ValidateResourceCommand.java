package com.sjiwon.anotherart.member.application.usecase.command;

import com.sjiwon.anotherart.member.domain.model.DuplicateResource;

public record ValidateResourceCommand(
        DuplicateResource resource,
        String value
) {
}
