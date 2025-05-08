package com.lgcns.domain.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageDirectory {
    POPUP("popup"),
    ITEM("item");

    private final String directory;
}
