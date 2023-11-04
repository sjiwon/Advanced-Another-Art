package com.sjiwon.anotherart.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import jakarta.servlet.http.HttpServletRequest;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sjiwon.anotherart.global.slack.SlackMetadata.DATETIME_FORMAT;
import static com.sjiwon.anotherart.global.slack.SlackMetadata.LOG_COLOR;
import static com.sjiwon.anotherart.global.slack.SlackMetadata.TITLE_ERROR_MESSAGE;
import static com.sjiwon.anotherart.global.slack.SlackMetadata.TITLE_REQUEST_IP;
import static com.sjiwon.anotherart.global.slack.SlackMetadata.TITLE_REQUEST_URL;
import static com.sjiwon.anotherart.global.slack.SlackMetadata.XFF_HEADER;
import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Slack SLACK_CLIENT = Slack.getInstance();

    private final ObjectMapper objectMapper;
    private final String slackWebhookUrl;

    public ApiExceptionHandler(final ObjectMapper objectMapper,
                               @Value("${slack.webhook.url}") final String slackWebhookUrl) {
        this.objectMapper = objectMapper;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    @ExceptionHandler(AnotherArtException.class)
    public ResponseEntity<ErrorResponse> anotherArtException(final AnotherArtException exception) {
        final ErrorCode code = exception.getCode();
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
    public ResponseEntity<ErrorResponse> unsatisfiedServletRequestParameterException(final UnsatisfiedServletRequestParameterException e) {
        return convert(GlobalErrorCode.VALIDATION_ERROR);
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(final MethodArgumentNotValidException e) throws JsonProcessingException {
        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(final BindException e) throws JsonProcessingException {
        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors));
    }

    private String extractErrorMessage(final List<FieldError> fieldErrors) throws JsonProcessingException {
        if (fieldErrors.size() == 1) {
            return fieldErrors.get(0).getDefaultMessage();
        }

        final Map<String, String> errors = new HashMap<>();
        for (final FieldError error : fieldErrors) {
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
    public ResponseEntity<ErrorResponse> runtimeException(final RuntimeException e, final HttpServletRequest request) {
        sendSlackAlertErrorLog(e, request);
        return convert(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(final Exception e, final HttpServletRequest request) {
        sendSlackAlertErrorLog(e, request);

        final ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        logging(code, e.getMessage());

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(final ErrorCode code) {
        logging(code);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(final ErrorCode code, final String message) {
        logging(code, message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(code, message));
    }

    private void logging(final ErrorCode code) {
        log.info(
                "statusCode={} || errorCode={} || message={}",
                code.getStatus().value(),
                code.getErrorCode(),
                code.getMessage()
        );
    }

    private void logging(final ErrorCode code, final String message) {
        log.info(
                "statusCode={} || errorCode={} || message={}",
                code.getStatus().value(),
                code.getErrorCode(),
                message
        );
    }

    public void sendSlackAlertErrorLog(final Exception e, final HttpServletRequest request) {
        try {
            SLACK_CLIENT.send(
                    slackWebhookUrl,
                    payload(p -> p
                            .text("서버 에러 발생!!")
                            .attachments(
                                    List.of(generateSlackErrorAttachments(e, request))
                            )
                    ));
        } catch (final IOException slackApiError) {
            log.error("Slack API 통신 간 에러 발생");
        }
    }

    private Attachment generateSlackErrorAttachments(final Exception e, final HttpServletRequest request) {
        final String requestTime = DateTimeFormatter.ofPattern(DATETIME_FORMAT).format(LocalDateTime.now());
        final String xffHeader = request.getHeader(XFF_HEADER);
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

    private Field generateSlackField(final String title, final String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }

    private String createRequestFullPath(final HttpServletRequest request) {
        String fullPath = request.getMethod() + " " + request.getRequestURL();

        final String queryString = request.getQueryString();
        if (queryString != null) {
            fullPath += "?" + queryString;
        }

        return fullPath;
    }
}
