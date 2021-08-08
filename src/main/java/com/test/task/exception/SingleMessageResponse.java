package com.test.task.exception;

import lombok.Getter;

public class SingleMessageResponse {

    @Getter
    private final String message;

    public SingleMessageResponse(String message) {
        this.message = message;
    }

}
