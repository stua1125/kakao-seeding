package org.antop.kakao.controller;

import lombok.extern.slf4j.Slf4j;
import org.antop.kakao.constants.Codes;
import org.antop.kakao.exception.AccessDeniedException;
import org.antop.kakao.exception.NotFoundException;
import org.antop.kakao.exception.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiResponse validation(NotFoundException e) {
        return ApiResponse.of(Codes.E0004.code, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ApiResponse validation(AccessDeniedException e) {
        return ApiResponse.of(Codes.E0003.code, e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiResponse validation(ValidationException e) {
        return ApiResponse.of(Codes.E0002.code, e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(ApiResponse.of(Codes.E0001.code, ex.getMessage()), headers, status);
    }
}
