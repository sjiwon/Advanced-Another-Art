package com.sjiwon.anotherart.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sjiwon.anotherart.global.slack.SlackMetadata.*;
import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Slack SLACK_CLIENT = Slack.getInstance();

    private final ObjectMapper objectMapper;
    private final String slackWebhookUrl;

    public ApiExceptionHandler(ObjectMapper objectMapper,
                               @Value("${slack.webhook.url}") String slackWebhookUrl) {
        this.objectMapper = objectMapper;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    @ExceptionHandler(AnotherArtException.class)
    public ResponseEntity<ErrorResponse> anotherArtException(AnotherArtException exception) {
        ErrorCode code = exception.getCode();
        logging(code);

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedException() {
        return convert(AuthErrorCode.INVALID_PERMISSION);
    }

    /**
     * 요청 파라미터 Validation 전용 ExceptionHandler
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> unsatisfiedServletRequestParameterException(UnsatisfiedServletRequestParameterException e) {
        return convert(GlobalErrorCode.VALIDATION_ERROR);
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) throws JsonProcessingException {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(BindException e) throws JsonProcessingException {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors));
    }

    private String extractErrorMessage(List<FieldError> fieldErrors) throws JsonProcessingException {
        if (fieldErrors.size() == 1) {
            return fieldErrors.get(0).getDefaultMessage();
        }

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return objectMapper.writeValueAsString(errors);
    }

    /**
     * 존재하지 않는 Endpoint 전용 ExceptionHandler
     */
    @ExceptionHandler({NoHandlerFoundException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> noHandlerFoundException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_URI_ERROR);
    }

    /**
     * HTTP Request Method 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_METHOD_ERROR);
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_MEDIA_TYPE_ERROR);
    }

    /**
     * 내부 서버 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException e, HttpServletRequest request) {
        sendSlackAlertErrorLog(e, request);
        return convert(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e, HttpServletRequest request) {
        sendSlackAlertErrorLog(e, request);

        ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        logging(code, e.getMessage());

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(ErrorCode code) {
        logging(code);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(ErrorCode code, String message) {
        logging(code, message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(code, message));
    }

    private void logging(ErrorCode code) {
        log.info(
                "statusCode={} || errorCode={} || message={}",
                code.getStatus().value(),
                code.getErrorCode(),
                code.getMessage()
        );
    }

    private void logging(ErrorCode code, String message) {
        log.info(
                "statusCode={} || errorCode={} || message={}",
                code.getStatus().value(),
                code.getErrorCode(),
                message
        );
    }

    public void sendSlackAlertErrorLog(Exception e, HttpServletRequest request) {
        try {
            SLACK_CLIENT.send(
                    slackWebhookUrl,
                    payload(p -> p
                            .text("서버 에러 발생!!")
                            .attachments(
                                    List.of(generateSlackErrorAttachments(e, request))
                            )
                    ));
        } catch (IOException slackApiError) {
            log.error("Slack API 통신 간 에러 발생");
        }
    }

    private Attachment generateSlackErrorAttachments(Exception e, HttpServletRequest request) {
        String requestTime = DateTimeFormatter.ofPattern(DATETIME_FORMAT).format(LocalDateTime.now());
        String xffHeader = request.getHeader(XFF_HEADER);
        return Attachment.builder()
                .color(LOG_COLOR)
                .title(requestTime + " 발생 에러 로그")
                .fields(
                        List.of(
                                generateSlackField(TITLE_REQUEST_IP, (xffHeader == null) ? request.getRemoteAddr() : xffHeader),
                                generateSlackField(TITLE_REQUEST_URL, createRequestFullPath(request)),
                                generateSlackField(TITLE_ERROR_MESSAGE, e.toString())
                        )
                )
                .build();
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }

    private String createRequestFullPath(HttpServletRequest request) {
        String fullPath = request.getMethod() + " " + request.getRequestURL();

        String queryString = request.getQueryString();
        if (queryString != null) {
            fullPath += "?" + queryString;
        }

        return fullPath;
    }
}
