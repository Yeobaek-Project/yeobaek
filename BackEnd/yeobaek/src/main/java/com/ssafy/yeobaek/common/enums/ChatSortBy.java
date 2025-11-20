package com.ssafy.yeobaek.common.enums;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;

public enum ChatSortBy {
    LATEST, // 최신순
    UNREAD;  // 안읽음순

    public static ChatSortBy fromString(String text) {
        for (ChatSortBy b : ChatSortBy.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new CustomException(ResponseCode.MESSAGE_INVALID_SORT);
    }
}
