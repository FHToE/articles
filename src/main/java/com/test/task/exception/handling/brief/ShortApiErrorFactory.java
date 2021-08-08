package com.test.task.exception.handling.brief;

import com.test.task.exception.handling.ApiError;
import com.test.task.exception.handling.ApiErrorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ShortApiErrorFactory implements ApiErrorFactory {
    @Override
    public ApiError create(HttpStatus status, String message, Exception ex) {
        return ShortApiError.of(status, message, ex);
    }

    @Override
    public ApiError create(HttpStatus status, Exception ex) {
        return ShortApiError.of(status, ex);
    }
}
