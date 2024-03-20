package com.sjiwon.anotherart.global.log;

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

    @Pointcut("""
                !execution(* com.sjiwon.anotherart.global.annotation..*(..))
                && !execution(* com.sjiwon.anotherart.global.aop..*(..))
                && !execution(* com.sjiwon.anotherart.global.base..*(..))
                && !execution(* com.sjiwon.anotherart.global.config..*(..))
                && !execution(* com.sjiwon.anotherart.global.decorator..*(..))
                && !execution(* com.sjiwon.anotherart.global.filter..*(..))
                && !execution(* com.sjiwon.anotherart.global.log..*(..))
                && !execution(* com.sjiwon.anotherart..*Config.*(..))
                && !execution(* com.sjiwon.anotherart..*Formatter.*(..))
                && !execution(* com.sjiwon.anotherart..*Properties.*(..))
                && !execution(* com.sjiwon.anotherart..*TokenProvider.*(..))
                && !execution(* com.sjiwon.anotherart..*TokenResponseWriter.*(..))
                && !execution(* com.sjiwon.anotherart..*TokenExtractor.*(..))
            """)
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
