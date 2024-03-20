package com.sjiwon.anotherart.global.log;

import org.springframework.stereotype.Component;

@Component
public class LoggingStatusManager {
    private final ThreadLocal<LoggingStatus> statusContainer = new ThreadLocal<>();

    public LoggingStatus getExistLoggingStatus() {
        final LoggingStatus status = statusContainer.get();
        if (status == null) {
            throw new IllegalStateException("ThreadLocal LoggingStatus not exists...");
        }
        return status;
    }

    public void syncStatus() {
        final LoggingStatus status = statusContainer.get();
        if (status == null) {
            statusContainer.set(new LoggingStatus());
        }
    }

    public void clearResource() {
        statusContainer.remove();
    }
}
