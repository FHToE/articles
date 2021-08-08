package com.test.task.exception.handling;

import org.springframework.http.HttpStatus;

public interface ApiError {
    HttpStatus getStatus();
    String getMessage();
}
