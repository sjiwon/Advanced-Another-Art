package com.sjiwon.anotherart.global.lock;

public interface LockManager {
    void acquire(final String key, final int timeout);

    void release(final String key);
}
