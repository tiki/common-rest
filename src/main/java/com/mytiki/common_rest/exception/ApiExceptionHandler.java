/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.common_rest.exception;

import com.mytiki.common_rest.reply.ApiReplyAmo;
import com.mytiki.common_rest.reply.ApiReplyAmoBuilder;
import com.mytiki.common_rest.reply.ApiReplyAmoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;

@Order
@ControllerAdvice
public abstract class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiReplyAmo<?>> handleException(Exception e, HttpServletRequest request){
        logger.error("Request: "
                + request.getRequestURI()
                + "caused {}", e);

        HashMap<String, String> errorProperties = new HashMap<>();
        if(e.getClass() != null) errorProperties.put("exception class", e.getClass().getName());
        if(e.getStackTrace() != null){
            errorProperties.put("stack class", e.getStackTrace()[0].getClassName());
            errorProperties.put("stack line", String.valueOf(e.getStackTrace()[0].getLineNumber()));
        }

        ApiReplyAmo<?> reply = new ApiReplyAmoBuilder<>()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .messages(new ApiReplyAmoMessage(e.getMessage(), errorProperties))
                .build();

        return ResponseEntity.status(reply.getCode()).body(reply);
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResponseEntity<ApiReplyAmo<?>> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request){
        logger.trace("Request: "
                + request.getRequestURI()
                + "caused {}", e);

        ApiReplyAmo<?> reply = new ApiReplyAmoBuilder<>()
                .httpStatus(HttpStatus.NOT_FOUND)
                .messages(new ApiReplyAmoMessage(e.getMessage()))
                .build();

        return ResponseEntity.status(reply.getCode()).body(reply);
    }

    @ExceptionHandler(value = {ServletException.class})
    public ResponseEntity<ApiReplyAmo<?>> handleServletException(ServletException e, HttpServletRequest request){
        logger.trace("Request: "
                + request.getRequestURI()
                + "caused {}", e);

        ApiReplyAmo<?> reply = new ApiReplyAmoBuilder<>()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .messages(new ApiReplyAmoMessage(e.getMessage()))
                .build();

        return ResponseEntity.status(reply.getCode()).body(reply);
    }

    @ExceptionHandler(value = {HttpClientErrorException.class})
    public ResponseEntity<ApiReplyAmo<?>> handleHttpClientErrorException(HttpClientErrorException e, HttpServletRequest request){
        if(e.getStatusCode().is5xxServerError())
            logger.error("Request: "
                    + request.getRequestURI()
                    + "caused {}", e);
        else
            logger.trace("Request: "
                    + request.getRequestURI()
                    + "caused {}", e);


        ApiReplyAmo<?> reply = new ApiReplyAmoBuilder<>()
                .httpStatus(e.getStatusCode())
                .messages(new ApiReplyAmoMessage(e.getMessage()))
                .build();

        return ResponseEntity.status(reply.getCode()).body(reply);
    }

    @ExceptionHandler(value = {HttpMessageConversionException.class})
    public ResponseEntity<ApiReplyAmo<?>> handleHttpMessageConversionException(HttpMessageConversionException e, HttpServletRequest request){
        logger.trace("Request: "
                + request.getRequestURI()
                + "caused {}", e);

        ApiReplyAmo<?> reply = new ApiReplyAmoBuilder<>()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .messages(new ApiReplyAmoMessage(e.getMessage()))
                .build();

        return ResponseEntity.status(reply.getCode()).body(reply);
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiReplyAmo<?>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request){
        logger.trace("Request: "
                + request.getRequestURI()
                + "caused {}", e);

        ApiReplyAmo<?> reply = new ApiReplyAmoBuilder<>()
                .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .messages(new ApiReplyAmoMessage(e.getMessage()))
                .build();

        return ResponseEntity.status(reply.getCode()).body(reply);
    }

    @ExceptionHandler(value = {ApiException.class})
    public ResponseEntity<ApiReplyAmo<?>> handleApiException(ApiException e, HttpServletRequest request){
        logger.debug("Request: "
                + request.getRequestURI()
                + "caused {}", e);

        ApiReplyAmo<?> reply = new ApiReplyAmoBuilder<>()
                .code(e.getCode())
                .status(e.getStatus())
                .messages(e.getMessages())
                .build();

        return ResponseEntity.status(reply.getCode()).body(reply);
    }
}
