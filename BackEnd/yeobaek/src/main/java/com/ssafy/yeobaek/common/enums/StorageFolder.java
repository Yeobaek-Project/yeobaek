package com.ssafy.yeobaek.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageFolder {
    IMAGES("images"),
    ARTWORK("artwork"),
    CAFE("cafe");

    private final String path;
}
