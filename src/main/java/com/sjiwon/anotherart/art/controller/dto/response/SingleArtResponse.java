package com.sjiwon.anotherart.art.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SingleArtResponse<T> {
    private T result;
}
