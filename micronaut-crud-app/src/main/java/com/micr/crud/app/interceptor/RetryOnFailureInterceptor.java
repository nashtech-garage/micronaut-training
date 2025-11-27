package com.micr.crud.app.interceptor;


import com.micr.crud.app.annotation.RetryOnFailure;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.AnnotationValue;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RetryOnFailureInterceptor implements MethodInterceptor<Object, Object> {

    private static final Logger log = LoggerFactory.getLogger(RetryOnFailureInterceptor.class);

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        AnnotationValue<RetryOnFailure> annotation =
                context.getAnnotationMetadata().getAnnotation(RetryOnFailure.class);
        if (annotation == null) {
            return context.proceed();
        }

        int maxAttempts = annotation.intValue("maxAttempts").orElse(3);
        long delay = annotation.longValue("delayMs").orElse(500L);

        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxAttempts) {
            try {
                attempt++;
                log.info("Attempt {} for method: {}", attempt, context.getMethodName());
                return context.proceed();
            } catch (Exception ex) {
                lastException = ex;
                log.warn("Attempt {} failed: {}", attempt, ex.getMessage());
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        log.error("All {} attempts failed", maxAttempts);
        throw new RuntimeException("Operation failed after retries", lastException);
    }
}