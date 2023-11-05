package com.sjiwon.anotherart.global.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final LoggingTracer loggingTracer;

    @Pointcut("execution(public * com.sjiwon.anotherart..*(..))")
    private void includeComponent() {
    }

    @Pointcut("!execution(public * com.sjiwon.anotherart.global.interceptor..*(..))")
    private void globalInterceptorPath() {
    }

    @Pointcut("!execution(public * com.sjiwon.anotherart.global.logging..*(..))")
    private void globalLoggingPath() {
    }

    @Pointcut("!execution(public * com.sjiwon.anotherart.global.config..*(..))")
    private void globalConfigPath() {
    }

    @Pointcut("globalInterceptorPath() && globalLoggingPath() && globalConfigPath()")
    private void excludeComponent() {
    }

    @Around("includeComponent() && excludeComponent()")
    public Object doLogging(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodSignature = joinPoint.getSignature().toShortString();
        final Object[] args = joinPoint.getArgs();
        loggingTracer.methodCall(methodSignature, args);
        try {
            final Object result = joinPoint.proceed();
            loggingTracer.methodReturn(methodSignature);
            return result;
        } catch (final Throwable e) {
            loggingTracer.throwException(methodSignature, e);
            throw e;
        }
    }
}
