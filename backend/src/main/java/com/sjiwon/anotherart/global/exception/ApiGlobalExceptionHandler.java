package com.sjiwon.anotherart.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.base.BusinessException;
import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import com.sjiwon.anotherart.global.exception.notify.SlackAlertManager;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sjiwon.anotherart.global.log.RequestMetadataExtractor.getRequestUriWithQueryString;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiGlobalExceptionHandler {
    private final ObjectMapper objectMapper;
    private final SlackAlertManager slackAlertManager;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> anotherArtException(final BusinessException exception) {
        final BusinessExceptionCode code = exception.getCode();
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedException(final AccessDeniedException e) {
        log.warn("AccessDeniedException Occurred", e);

        final BusinessExceptionCode code = AuthErrorCode.INVALID_PERMISSION;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    /**
     * JSON Parsing Error 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> httpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException Occurred", e);

        final BusinessExceptionCode code = GlobalExceptionCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    /**
     * 요청 파라미터 Validation 전용 ExceptionHandler
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> unsatisfiedServletRequestParameterException(final UnsatisfiedServletRequestParameterException e) {
        log.warn("UnsatisfiedServletRequestParameterException Occurred", e);

        final BusinessExceptionCode code = GlobalExceptionCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException Occurred", e);

        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        final BusinessExceptionCode code = GlobalExceptionCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.of(code, extractErrorMessage(fieldErrors)));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> bindException(final BindException e) {
        log.warn("BindException Occurred", e);

        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        final BusinessExceptionCode code = GlobalExceptionCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.of(code, extractErrorMessage(fieldErrors)));
    }

    private String extractErrorMessage(final List<FieldError> fieldErrors) {
        if (fieldErrors.size() == 1) {
            return fieldErrors.get(0).getDefaultMessage();
        }

        final Map<String, String> errors = new HashMap<>();
        for (final FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        try {
            return objectMapper.writeValueAsString(errors);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("JSON Parsing Error...", e);
        }
    }

    /**
     * 존재하지 않는 Endpoint 전용 ExceptionHandler
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> noHandlerFoundException(final NoHandlerFoundException e) {
        log.warn("NoHandlerFoundException Occurred", e);

        final BusinessExceptionCode code = GlobalExceptionCode.NOT_SUPPORTED_URI_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    /**
     * Method Argument Exception 전용 ExceptionHandler
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException Occurred", e);

        final BusinessExceptionCode code = GlobalExceptionCode.NOT_SUPPORTED_URI_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    /**
     * HTTP Request Method 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> httpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.warn("HttpRequestMethodNotSupportedException Occurred", e);

        final BusinessExceptionCode code = GlobalExceptionCode.NOT_SUPPORTED_METHOD_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> httpMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException e) {
        log.warn("HttpMediaTypeNotSupportedException Occurred", e);

        final BusinessExceptionCode code = GlobalExceptionCode.UNSUPPORTED_MEDIA_TYPE_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    /**
     * 내부 서버 오류 전용 ExceptionHandler (With Slack Alert)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleAnyException(final HttpServletRequest request, final RuntimeException e) {
        log.error(
                "RuntimeException Occurred -> {} {}",
                request.getMethod(),
                getRequestUriWithQueryString(request),
                e
        );
        slackAlertManager.sendErrorLog(request, e);

        final BusinessExceptionCode code = GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    /**
     * Exception (With Slack Alert)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAnonymousException(final HttpServletRequest request, final Exception e) {
        log.error(
                "Unknown Exception Occurred -> {} {}",
                request.getMethod(),
                getRequestUriWithQueryString(request),
                e
        );
        slackAlertManager.sendErrorLog(request, e);

        final BusinessExceptionCode code = GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }
}
