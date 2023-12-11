package com.sjiwon.anotherart.global.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

        int currentRetry = 0;
        while (true) {
            try {
                if (tryMaximum(currentRetry++, distributedLock, method)) {
                    throw new RuntimeException("Retry Exception...");
                }

                acquireLock(lock, distributedLock);

                log.info(
                        "Thread[{}] -> [{}] lock acquired with in transaction = {}",
                        Thread.currentThread().getName(),
                        lock.getName(),
                        distributedLock.withInTransaction()
                );

                if (distributedLock.withInTransaction()) {
                    return aopWithTransactional.proceed(joinPoint);
                }
                return joinPoint.proceed();
            } catch (final ObjectOptimisticLockingFailureException e) {
                log.info(
                        "[{}] Optimistic Lock Version Miss... -> retry = {}, maxRetry = {}, withInTransaction = {}",
                        Thread.currentThread().getName(),
                        currentRetry,
                        distributedLock.withRetry(),
                        distributedLock.withInTransaction()
                );
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (final InterruptedException e) {
                throw new RuntimeException("Interrupt occurred when acquire lock...", e);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            } finally {
                release(lock);
            }
        }
    }

    private boolean tryMaximum(final int currentRetry, final DistributedLock distributedLock, final Method method) {
        if (distributedLock.withRetry() != -1 && distributedLock.withRetry() == currentRetry) {
            log.error("Retry Exception... -> method = {}", method);
            return true;
        }
        return false;
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
                    "[{}] Alreay unlock or timeout... -> isLocked = {} || isHeldByCurrentThread = {}",
                    Thread.currentThread().getName(),
                    lock.isLocked(),
                    lock.isHeldByCurrentThread()
            );
            throw new RuntimeException("anonymous try unlock or timeout...");
        }
    }
}
