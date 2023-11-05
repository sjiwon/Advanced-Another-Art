package com.sjiwon.anotherart.global.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.sjiwon.anotherart.global.logging.LoggingStatus;
import com.sjiwon.anotherart.global.logging.LoggingStatusManager;
import com.sjiwon.anotherart.global.logging.RequestMetaData;
import com.sjiwon.anotherart.global.security.filter.ReadableRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import static com.sjiwon.anotherart.global.logging.RequestMetadataExtractor.getClientIP;
import static com.sjiwon.anotherart.global.logging.RequestMetadataExtractor.getHttpMethod;
import static com.sjiwon.anotherart.global.logging.RequestMetadataExtractor.getRequestUriWithQueryString;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLogInterceptor implements HandlerInterceptor {
    private static final String[] INFRA_URI = {"/favicon.ico", "/error*", "/swagger*", "/actuator*"};

    private final LoggingStatusManager loggingStatusManager;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws IOException {
        if (CorsUtils.isPreFlightRequest(request) || isInfraUri(request)) {
            return true;
        }

        loggingStatusManager.syncStatus();
        final RequestMetaData requestMetaData = new RequestMetaData(loggingStatusManager.getTaskId(), request);
        log.info("[Request START] -> [{}]", requestMetaData);
        log.info(
                "[{}] Request Body = {}",
                loggingStatusManager.getTaskId(),
                readRequestBodyViaCachingRequestWrapper(request)
        );

        return true;
    }

    @Override
    public void afterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception ex
    ) throws IOException {
        if (CorsUtils.isPreFlightRequest(request) || isInfraUri(request)) {
            return;
        }

        final LoggingStatus loggingStatus = loggingStatusManager.getExistLoggingStatus();
        final long totalTime = loggingStatus.totalTakenTime();
        log.info(
                "[{}] Response Body = {}",
                loggingStatusManager.getTaskId(),
                readResponseBodyViaCachingRequestWrapper(response)
        );
        log.info(
                "[Request END] -> [Task ID = {}, IP = {}, HTTP Method = {}, Uri = {}, HTTP Status = {}, 요청 처리 시간 = {}ms]",
                loggingStatus.getTaskId(),
                getClientIP(request),
                getHttpMethod(request),
                getRequestUriWithQueryString(request),
                response.getStatus(),
                totalTime
        );

        loggingStatusManager.clearResource();
    }

    private boolean isInfraUri(final HttpServletRequest request) {
        return PatternMatchUtils.simpleMatch(INFRA_URI, request.getRequestURI());
    }

    private JsonNode readRequestBodyViaCachingRequestWrapper(final HttpServletRequest request) throws IOException {
        if (request instanceof final ReadableRequestWrapper requestWrapper) {
            final byte[] bodyContents = requestWrapper.getContentAsByteArray();

            if (bodyContents.length == 0) {
                return NullNode.getInstance();
            }
            return objectMapper.readTree(bodyContents);
        }
        return NullNode.getInstance();
    }

    private JsonNode readResponseBodyViaCachingRequestWrapper(final HttpServletResponse response) throws IOException {
        if (response instanceof final ContentCachingResponseWrapper responseWrapper) {
            final byte[] bodyContents = responseWrapper.getContentAsByteArray();

            if (bodyContents.length == 0) {
                return NullNode.getInstance();
            }
            return objectMapper.readTree(bodyContents);
        }
        return NullNode.getInstance();
    }
}
