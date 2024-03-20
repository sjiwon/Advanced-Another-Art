package com.sjiwon.anotherart.global.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingTracer {
    private static final String REQUEST_PREFIX = "--->";
    private static final String RESPONSE_PREFIX = "<---";
    private static final String EXCEPTION_PREFIX = "<X--";

    private final LoggingStatusManager loggingStatusManager;

    public void methodCall(
            final String methodSignature,
            final Object[] args
    ) {
        loggingStatusManager.syncStatus();

        final LoggingStatus loggingStatus = loggingStatusManager.getExistLoggingStatus();
        loggingStatus.increaseDepth();

        if (log.isInfoEnabled()) {
            log.info(
                    "{} args={}",
                    loggingStatus.depthPrefix(REQUEST_PREFIX) + methodSignature,
                    args
            );
        }
    }

    public void methodReturn(final String methodSignature) {
        final LoggingStatus loggingStatus = loggingStatusManager.getExistLoggingStatus();
        if (log.isInfoEnabled()) {
            log.info(
                    "{} time={}ms",
                    loggingStatus.depthPrefix(RESPONSE_PREFIX) + methodSignature,
                    loggingStatus.calculateTakenTime()
            );
        }
        loggingStatus.decreaseDepth();
    }

    public void throwException(final String methodSignature, final Throwable exception) {
        final LoggingStatus loggingStatus = loggingStatusManager.getExistLoggingStatus();
        if (log.isInfoEnabled()) {
            log.info(
                    "{} time={}ms ex={}",
                    loggingStatus.depthPrefix(EXCEPTION_PREFIX) + methodSignature,
                    loggingStatus.calculateTakenTime(),
                    exception.toString()
            );
        }
        loggingStatus.decreaseDepth();
    }
}
