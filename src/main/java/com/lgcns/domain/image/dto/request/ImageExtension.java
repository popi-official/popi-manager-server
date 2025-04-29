package com.lgcns.domain.image.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageExtension {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    WEBP("webp"),
    ;

    private final String extension;
}
