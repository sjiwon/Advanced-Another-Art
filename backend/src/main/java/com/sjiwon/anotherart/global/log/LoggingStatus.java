package com.sjiwon.anotherart.global.log;

import lombok.Getter;

@Getter
public class LoggingStatus {
    private final long startTimeMillis;
    private int depthLevel = 0;

    public LoggingStatus() {
        this.startTimeMillis = System.currentTimeMillis();
    }

    public void increaseDepth() {
        depthLevel++;
    }

    public void decreaseDepth() {
        depthLevel--;
    }

    public String depthPrefix(final String prefixString) {
        if (depthLevel == 1) {
            return "|" + prefixString;
        }
        final String bar = "|" + " ".repeat(prefixString.length());
        return bar.repeat(depthLevel - 1) + "|" + prefixString;
    }

    public long calculateTakenTime() {
        return System.currentTimeMillis() - startTimeMillis;
    }
}
