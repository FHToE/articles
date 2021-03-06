package com.test.task.exception.handling;

import com.test.task.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
@AllArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final ApiErrorFactory errorFactory;

    private static ResponseEntity<Object> responseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class, ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFound(Exception ex) {
        return responseEntity(errorFactory.create(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return responseEntity(errorFactory.create(HttpStatus.FORBIDDEN, ex.getMessage(), ex));
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        return responseEntity(errorFactory.create(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex));
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest ignored) {
        log.error("Error", ex);
        return responseEntity(errorFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }
}
