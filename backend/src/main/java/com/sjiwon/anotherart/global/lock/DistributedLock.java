package com.sjiwon.anotherart.global.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String keyPrefix();

    String keySuffix();

    long waitTime() default 5000L;

    long leaseTime() default 3000L;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    boolean withInTransaction() default false;
}
