package com.sjiwon.anotherart.global.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {
    private final RedissonClient redissonClient;
    private final AopWithTransactional aopWithTransactional;

    @Around("@annotation(DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        final String key = (String) DistributedLockNameGenerator.generate(
                distributedLock.keyPrefix(),
                distributedLock.keySuffix(),
                signature.getParameterNames(),
                joinPoint.getArgs()
        );
        final RLock lock = redissonClient.getLock(key);

        try {
            acquireLock(lock, distributedLock);

            log.info(
                    "Thread[{}] -> [{}] lock acquired with in transaction : {}",
                    Thread.currentThread().getName(),
                    lock.getName(),
                    distributedLock.withInTransaction()
            );

            if (distributedLock.withInTransaction()) {
                return aopWithTransactional.proceed(joinPoint);
            }
            return joinPoint.proceed();
        } catch (final InterruptedException e) {
            throw new RuntimeException("Interrupt occurred when acquire lock...", e);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        } finally {
            release(lock);
        }
    }

    private void acquireLock(final RLock lock, final DistributedLock distributedLock) throws InterruptedException {
        if (!tryLock(lock, distributedLock)) {
            throw new RuntimeException("Failed to acquire lock...");
        }
    }

    private boolean tryLock(final RLock lock, final DistributedLock distributedLock) throws InterruptedException {
        return lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
    }

    private void release(final RLock lock) {
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
                log.info("Thread[{}] -> [{}] lock released", Thread.currentThread().getName(), lock.getName());
            } catch (final Throwable e) {
                log.error("Failed to release lock", e);
            }
        } else {
            log.error(
                    "Alreay unlock or timeout... -> isLocked = {} || isHeldByCurrentThread = {}",
                    lock.isLocked(),
                    lock.isHeldByCurrentThread()
            );
            throw new RuntimeException("anonymous try unlock or timeout...");
        }
    }
}
